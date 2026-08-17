USE [SingleHotelManagementDB];
GO

SET XACT_ABORT ON;
GO

BEGIN TRANSACTION;

IF COL_LENGTH(N'dbo.users', N'failed_login_attempts') IS NULL
BEGIN
    ALTER TABLE dbo.[users]
        ADD failed_login_attempts INT NOT NULL
            CONSTRAINT DF_users_failed_login_attempts DEFAULT (0);
END;

IF COL_LENGTH(N'dbo.users', N'locked_until') IS NULL
BEGIN
    ALTER TABLE dbo.[users]
        ADD locked_until DATETIME2(0) NULL;
END;

IF NOT EXISTS
(
    SELECT 1
      FROM sys.check_constraints
     WHERE parent_object_id = OBJECT_ID(N'dbo.users')
       AND [name] = N'CK_users_failed_login_attempts'
)
BEGIN
    EXEC(N'ALTER TABLE dbo.[users]
        ADD CONSTRAINT CK_users_failed_login_attempts
            CHECK (failed_login_attempts >= 0);');
END;

COMMIT TRANSACTION;
GO
