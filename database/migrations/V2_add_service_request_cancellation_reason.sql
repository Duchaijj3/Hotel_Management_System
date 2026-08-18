IF COL_LENGTH('dbo.service_requests', 'cancellation_reason') IS NULL
BEGIN
ALTER TABLE dbo.service_requests
    ADD cancellation_reason NVARCHAR(1000) NULL;
END;
GO