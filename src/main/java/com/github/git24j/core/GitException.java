package com.github.git24j.core;

/** @author shijing */
public class GitException extends RuntimeException {

    private final String message;
    private ErrorCode code;
    private ErrorClass klass;

    public GitException(int klass, String message) {
        this(ErrorClass.of(klass), message);
    }

    public GitException(ErrorClass klass, String message) {
        super(message);
        this.klass = klass;
        this.message = message;
    }

    public GitException() {
        super();
        this.klass = null;
        this.message = "";
    }

    /**
     * @see {@code git_error_t }
     * @return git error class or null if we could not retrieve error class.
     */
    public ErrorClass getErrorClass() {
        return klass;
    }

    public ErrorCode getCode() {
        return code;
    }

    public void setCode(ErrorCode code) {
        this.code = code;
    }

    public void setCode(int rawCode) {
        code = ErrorCode.of(rawCode);
    }

    public enum ErrorCode {
        /** < No error */
        OK(0),
        /** < Generic error */
        ERROR(-1),
        /** < Requested object could not be found */
        ENOTFOUND(-3),
        /** < Object exists preventing operation */
        EEXISTS(-4),
        /** < More than one object matches */
        EAMBIGUOUS(-5),
        /** < Output buffer too short to hold data */
        EBUFS(-6),
        /**
         * GIT_EUSER is a special error that is never generated by libgit2 code. You can return it
         * from a callback (e.g to stop an iteration) to know that it was generated by the callback
         * and not by libgit2.
         */
        EUSER(-7),
        /** < Operation not allowed on bare repository */
        EBAREREPO(-8),
        /** < HEAD refers to branch with no commits */
        EUNBORNBRANCH(-9),
        /** < Merge in progress prevented operation */
        EUNMERGED(-10),
        /** < Reference was not fast-forwardable */
        ENONFASTFORWARD(-11),
        /** < Name/ref spec was not in a valid format */
        EINVALIDSPEC(-12),
        /** < Checkout conflicts prevented operation */
        ECONFLICT(-13),
        /** < Lock file prevented operation */
        ELOCKED(-14),
        /** < Reference value does not match expected */
        EMODIFIED(-15),
        /** < Authentication error */
        EAUTH(-16),
        /** < Server certificate is invalid */
        ECERTIFICATE(-17),
        /** < Patch/merge has already been applied */
        EAPPLIED(-18),
        /** < The requested peel operation is not possible */
        EPEEL(-19),
        /** < Unexpected EOF */
        EEOF(-20),
        /** < Invalid operation or input */
        EINVALID(-21),
        /** < Uncommitted changes in index prevented operation */
        EUNCOMMITTED(-22),
        /** < The operation is not valid for a directory */
        EDIRECTORY(-23),
        /** < A merge conflict exists and cannot continue */
        EMERGECONFLICT(-24),
        /** < A user-configured callback refused to act */
        PASSTHROUGH(-30),
        /** < Signals end of iteration with iterator */
        ITEROVER(-31),
        /** < Internal only */
        RETRY(-32),
        /** < Hashsum mismatch in object */
        EMISMATCH(-33),
        /** < Unsaved changes in the index would be overwritten */
        EINDEXDIRTY(-34),
        /** < Patch application failed */
        EAPPLYFAIL(-35),
        /** < undefined code in the libgit2, because of a version mismatch? */
        UNKNOWN(-9999);
        private final int code;

        ErrorCode(int code) {
            this.code = code;
        }

        public static ErrorCode of(int gitErrorCode) {
            for (ErrorCode c : ErrorCode.values()) {
                if (c.code == gitErrorCode) {
                    return c;
                }
            }
            return UNKNOWN;
        }

        /** libgit2 error code in integer. */
        public int getCode() {
            return code;
        }
    }

    /** Error classes */
    public enum ErrorClass {
        NONE,
        NOMEMORY,
        OS,
        INVALID,
        REFERENCE,
        ZLIB,
        REPOSITORY,
        CONFIG,
        REGEX,
        ODB,
        INDEX,
        OBJECT,
        NET,
        TAG,
        TREE,
        INDEXER,
        SSL,
        SUBMODULE,
        THREAD,
        STASH,
        CHECKOUT,
        FETCHHEAD,
        MERGE,
        SSH,
        FILTER,
        REVERT,
        CALLBACK,
        CHERRYPICK,
        DESCRIBE,
        REBASE,
        FILESYSTEM,
        PATCH,
        WORKTREE,
        SHA1;

        /**
         * Derive ErrorClass from ordinal index.
         *
         * @param klass ordinal value
         * @return ErrorClass or null
         */
        static ErrorClass of(int klass) {
            ErrorClass[] values = ErrorClass.values();
            if (klass >= 0 && klass < values.length) {
                return values[klass];
            }
            return null;
        }
    }
}