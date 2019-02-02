

# shop feature datastructure
*  base url /communities/$communityReference/features/shops
* pools:
  * $pushID_pools
    * status : AC/UP
      * diliveryTime :
      * poolID :
      * shopID :
      * name :
      * offerType :
      * description :
      * imageURL :
      * upvote :
      * totalOrdered : number
  * ...
* archived_pools:
  * $pushID_pools
    * status : AC/UP
    * diliveryTime :
    * poolID :
    * shopID :
    * name :
    * offerType :
    * description :
    * imageURL :
    * upvote :
    * totalOrdered : number
    
  * ....

*  base url /communities/$communityReference/
* shopOwner:
  * $shop_id
    * info :
      * shopName :
      * ownerName :
      * number :
      * email :
    * pools:
      * poolInfo :
        * $pool_ID :
          * name :
          * offerType :
          * description :
          * imageURL :
      * poolDetails :
        * $item_id:
          * name :
          * imageURL :
          * description :
        * ...
        
         * ...
  * ....
  
