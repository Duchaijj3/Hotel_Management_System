SET NOCOUNT ON;
SET XACT_ABORT ON;

BEGIN TRANSACTION;

UPDATE room
   SET operational_status = 'OUT_OF_SERVICE',
       updated_at = SYSUTCDATETIME()
  FROM dbo.rooms room
  JOIN dbo.room_types type
    ON type.room_type_id = room.room_type_id
 WHERE type.is_active = 0
   AND room.operational_status <> 'OCCUPIED';

COMMIT TRANSACTION;
