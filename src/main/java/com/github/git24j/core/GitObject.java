package com.github.git24j.core;

import java.util.concurrent.atomic.AtomicLong;

/** Generic git object. */
public class GitObject implements AutoCloseable {
    private final AtomicLong rawPtr = new AtomicLong();

    protected GitObject(long rawPointer) {
        rawPtr.set(rawPointer);
    }

    static native int jniFree(long objPtr);

    static native int jniType(long objPtr);

    static native long jniId(long objPtr);

    static native int jniShortId(Buf buf, long objPtr);

    static native int jniLookup(AtomicLong outObj, long repoPtr, long oidPtr, int objType);

    static native int jniLookupPrefix(
            AtomicLong outObj, long repoPtr, long oidPtr, int len, int objType);

    static native long jniOwner(long objPtr);

    static native int jniPeel(AtomicLong outObj, long objPtr, int objType);

    static native int jniDup(AtomicLong outObj, long objPtr);

    /**
     * Create Right object according to GitObject's type. We need this because c does not have
     * inheritance hierarchy.
     *
     * @throws IllegalStateException if object is invalid or has invalid memory address.
     */
    static GitObject create(long objPtr) {
        if (objPtr == 0) {
            throw new IllegalStateException("object address is NULL, has it been closed?");
        }
        switch (Type.valueOf(jniType(objPtr))) {
            case INVALID:
                throw new IllegalStateException("invalid git object");
            case COMMIT:
                return new Commit(objPtr);
            case BLOB:
                return new Blob(objPtr);
            case TAG:
                return new Tag(objPtr);
            default: // TODO: make sure types are exhaustive.
                return new GitObject(objPtr);
        }
    }

    /**
     * Lookup a reference to one of the objects in a repository.
     *
     * @param repository the repository to look up the object
     * @param oid the unique identifier for the object
     * @param type the type of the object
     * @return found object
     * @throws GitException git errors
     * @throws IllegalStateException required objects are not open or have been closed.
     */
    public static GitObject lookup(Repository repository, Oid oid, Type type) {
        AtomicLong outObj = new AtomicLong();
        Error.throwIfNeeded(
                jniLookup(outObj, repository.getRawPointer(), oid.getRawPointer(), type.value));
        return GitObject.create(outObj.get());
    }

    /**
     * TODO: passing len can't be the right thing, change this once {@code Oid} is designed
     * properly. Lookup a reference to one of the objects in a repository, given a prefix of its
     * identifier (short id).
     *
     * @param repository the repository to look up the object
     * @param oid a short identifier for the object
     * @param len the length of the short identifier
     * @param type the type of the object
     * @return looked-up object
     */
    public static GitObject lookupPrefix(Repository repository, Oid oid, int len, Type type) {
        AtomicLong outObj = new AtomicLong();
        Error.throwIfNeeded(
                jniLookupPrefix(
                        outObj, repository.getRawPointer(), oid.getRawPointer(), len, type.value));
        return GitObject.create(outObj.get());
    }

    /**
     * Get raw pointer of the repo.
     *
     * @return pointer value in long
     * @throws IllegalStateException if repository has already been closed.
     */
    long getRawPointer() {
        long ptr = rawPtr.get();
        if (ptr == 0) {
            throw new IllegalStateException(
                    "Object has invalid memory address, likely it has been closed.");
        }
        return ptr;
    }

    /** Free the given reference. */
    @Override
    public void close() {
        jniFree(rawPtr.getAndSet(0));
    }

    /** TODO: change to type() Get the object type of an object. */
    public Type type() {
        return Type.valueOf(jniType(rawPtr.get()));
    }

    /**
     * Get the id (SHA1) of a repository object
     *
     * @return the SHA1 id
     */
    public Oid id() {
        return new Oid(jniId(rawPtr.get()));
    }

    /**
     * Get a short abbreviated OID string for the object
     *
     * @return Buffer that short id string was written into.
     * @throws GitException git error.
     */
    public Buf shortId() {
        Buf buf = new Buf();
        Error.throwIfNeeded(jniShortId(buf, rawPtr.get()));
        return buf;
    }

    /**
     * Recursively peel an object until an object of the specified type is met.
     *
     * @param targetType The {@link GitObject.Type} of the requested object.
     * @return Peeled GitObject (need to be closed to avoid resource leak).
     */
    public GitObject peel(Type targetType) {
        AtomicLong outPtr = new AtomicLong();
        Error.throwIfNeeded(jniPeel(outPtr, getRawPointer(), targetType.value));
        return new GitObject(outPtr.get());
    }

    /**
     * Create an copy of a Git object. The copy must be explicitly closed or it will leak.
     *
     * @return copy of the object.
     */
    public GitObject dup() {
        AtomicLong out = new AtomicLong();
        Error.throwIfNeeded(jniDup(out, getRawPointer()));
        return new GitObject(out.get());
    }

    /**
     * Get the repository that owns this object. Calling close on the returned pointer will
     * invalidate the actual object.
     *
     * @return the repository who owns this object
     */
    public Repository owner() {
        return Repository.ofRaw(jniOwner(rawPtr.get()));
    }

    public enum Type {
        ANY(-2),
        INVALID(-1),
        COMMIT(1),
        TREE(2),
        BLOB(3),
        TAG(4),
        OFS_DELTA(6),
        ERF_DELTA(7),
        ;
        private final int value;

        private Type(int value) {
            this.value = value;
        }

        static Type valueOf(int iVal) {
            for (Type x : Type.values()) {
                if (x.value == iVal) {
                    return x;
                }
            }
            return INVALID;
        }

        /** Get associated value. */
        public int getValue() {
            return value;
        }
    }
}
