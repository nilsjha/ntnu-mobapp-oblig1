# TODO

## API
* MarketplaceService 
  * Setup SSL Encryption and renewal at proxy stage

## Business logic
* Attachment bean
  * Implement functionality to delete attachment (single removal)
  * ~~Implement delete file on disk when attachment is removed from DB~~
  * Migrate folder exsists check to startup of server
  * ~~After Item has been sold: disable upload/delete of attachment~~
* Item bean
  * ~~Implement delete attachment on item delete~~
  * List: Implement SQL/logic to display published items in date range (bonus)
* Mail bean
  * Get rid of slowness of reply when sending mail (currently affecting purchases)

## General
* Migrate debug prints to a logger instead of system.out prints
* ~~Setup docker-compose.yml for both JavaEE & PostgreSQL~~

## Database / Storage
* Make photos path persistent

