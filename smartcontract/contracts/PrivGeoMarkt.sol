pragma solidity ^0.4.24;

/// @title Accountable, Searchable Encrypted Location Collection.
contract PrivGeoMarkt {
    event offerMade(address owner, address buyer);
    event sendKeysEvent(address owner, address buyer, bytes encryptedKeys, uint numEncryptedKeys);

    constructor() public {
    }

    /********************************************** PURCHASE POLICY ******************************/
    /// Policy for purchase
    struct PurchasePolicy {
        uint minPrice; // minimum price that this owner accepts for each transaction
    }

    mapping(address => PurchasePolicy) public purchasePolicies; //purchase policy of each owner

    function getPurchasePolicyOfOwner(address owner) public view returns(uint minPrice) {
        return purchasePolicies[owner].minPrice;
    }

    /// An owner creates a purchase policy for him.
    /// Update the current purchase policy if any.
    /// Parameters:
    ///   - minPrice is in wei.
    /// Returns:
    ///   - index of the purchase policy of this user
    function createPurchasePolicy(uint minPrice_) public {
        purchasePolicies[msg.sender].minPrice = minPrice_;
    }

    /// A user deletes his own purchase policy
    function deletePurchasePolicy() public {
        delete purchasePolicies[msg.sender];
    }

    /// Does this value satisfy owner's purchase policy
    function isSatisfiable(address owner, bytes32[] dataAddresses, uint value) public view returns (bool satisfied) {
        return purchasePolicies[owner].minPrice * dataAddresses.length <= value;
    }

    /********************************************** BUYER PUBLIC KEYS ******************************/

    mapping(address => bytes) public buyerPublicKey;

    /// get public key of a buyer
    function getPublicKeyOfBuyer(address buyer) public view returns(bytes pk) {
        return buyerPublicKey[buyer];
    }

    /// A buyer set his public key
    function setPublicKey(bytes pk) public {
        buyerPublicKey[msg.sender] = pk;
    }

    /********************************************** OFFER ******************************/
    enum OfferStatus {INVALID, PENDING, KEYS_SUBMITTED, CANCELED, WITHDRAWN, REFUNDED}

    struct Offer {
        address buyer;
        address owner;
        uint value; //value (or price) of the offer
        uint offerExpiration; // expiration timestamp for an offer
        uint refundExpiration; // expiration timestamp for a refund
        bytes32[] dataAddresses;
        // encrypted decryption keys from the owner.
        //This is a combination of all keys. So those keys should be derived correctly based on the numEncryptedKeys
        bytes encryptedKeys;
        uint numEncryptedKeys; //number of encrypted decryption keys from the owner.

        uint inOwnerIndex; //index in the list of offers of this owner
        uint inBuyerIndex; //index in the list of offers of this buyer

        OfferStatus status;
    }

    Offer[] public offers;

    mapping(address => uint[]) public offersOfOwner; // indices in the main list of offers of each owner
    mapping(address => uint[]) public offersOfBuyer; // indices in the main list of offers of each buyer

    /// Getters
    function getNumOffersOfBuyer(address buyer) public view returns (uint numOffers) {
        return offersOfBuyer[buyer].length;
    }

    function getNumOffersOfOwners(address owner) public view returns (uint numOffers) {
        return offersOfOwner[owner].length;
    }

    function getOfferIndicesOfBuyers(address buyer) public view returns (uint[] indices) {
        return offersOfOwner[buyer];
    }

    function getOfferIndicesOfOwners(address owner) public view returns (uint[] indices) {
        return offersOfOwner[owner];
    }

    // Total of offers
    function getNumOffers() public view returns (uint numOffers) {
        return offers.length;
    }

    function getOfferContributors(uint indexInOffersList)
    public view
    returns (address buyer,
        address owner,
        uint value,
        uint inOwnerIndex,
        uint inBuyerIndex,
        bytes32[] dataAddresses) {
        require(indexInOffersList < offers.length, "Invalid index of the offer in the offer list to be selected");
        return (offers[indexInOffersList].buyer,
            offers[indexInOffersList].owner,
            offers[indexInOffersList].value,
            offers[indexInOffersList].inOwnerIndex,
            offers[indexInOffersList].inBuyerIndex,
            offers[indexInOffersList].dataAddresses);
    }

    function getOfferExpirationTimes(uint indexInOffersList)
    public view
    returns (uint offerExpiration, uint refundExpiration) {
        require(indexInOffersList < offers.length, "Invalid index of the offer in the offer list to be selected");
        return (offers[indexInOffersList].offerExpiration, offers[indexInOffersList].refundExpiration);
    }

    function getOfferKeys(uint indexInOffersList)
    public view
    returns (bytes encryptedKeys, uint numEncryptedKeys) {
        require(indexInOffersList < offers.length, "Invalid index of the offer in the offer list to be selected");
        return (offers[indexInOffersList].encryptedKeys, offers[indexInOffersList].numEncryptedKeys);
    }

    function getOfferStatus(uint indexInOffersList)
    public view
    returns (OfferStatus status) {
        require(indexInOffersList < offers.length, "Invalid index of the offer in the offer list to be selected");
        return (offers[indexInOffersList].status);
    }

    /// A buyer purchases a set of id(s) of 1 owner
    function makeOffer(address owner, bytes32[] dataAddresses_, uint offerExpiration_, uint refundExpiration_)
    public payable
    {
        // check conditions
        require(0 < dataAddresses_.length, "Empty data items list");
        require(offerExpiration_ < refundExpiration_, "Offer exipration should be earlier than Refund expiration");

        address buyer = msg.sender;

        require(isSatisfiable(owner, dataAddresses_, msg.value), "Purchase policy is not satisfied");

        // effect - content of an offer
        Offer memory offer;
        offer.buyer = buyer;
        offer.owner = owner;
        offer.value = msg.value;
        offer.offerExpiration = offerExpiration_;
        offer.refundExpiration = refundExpiration_;
        offer.dataAddresses = dataAddresses_;

        uint offerIndex = offers.length; //this offer WILL be added to the end
        offer.inOwnerIndex = offersOfOwner[owner].push(offerIndex) - 1; // make sure that OWNER can find his offer
        offer.inBuyerIndex = offersOfBuyer[buyer].push(offerIndex) - 1; // make sure that BUYER can find his offer

        offer.status = OfferStatus.PENDING;  // set status of offer

        offers.push(offer);  // add offer to the offer list

//        emit offerMade(owner, buyer);  // emit an event
    }


    // Is the mapping between index and value in offer list and in buyer's and owner's offer lists correct?
    function isValidOfferMapping(uint indexInOffersList) public view returns (bool){
        //Invalid index of the offer in the offer list to be deleted
        if (indexInOffersList >= offers.length) return false;

        address buyer = offers[indexInOffersList].buyer;
        address owner = offers[indexInOffersList].owner;

        //Invalid index of the offer in the offer list of buyer
        uint indexInBuyerList = offers[indexInOffersList].inBuyerIndex;
        if (indexInBuyerList >= offersOfBuyer[buyer].length)
            return false;
        if (indexInOffersList != offersOfBuyer[buyer][indexInBuyerList])
            return false;

        //Invalid index of the offer in the offer list of owner to be deleted
        uint indexInOwnerList = offers[indexInOffersList].inOwnerIndex;
        if (indexInOwnerList >= offersOfOwner[owner].length)
            return false;
        if (indexInOffersList != offersOfOwner[owner][indexInOwnerList])
            return false;

        return true;
    }


    // a buyer cancels his offer at an index in HIS list of offers
    // - remove the offer if exists
    // - refund the amount of the offer if any
    // NOTE: buyer can only cancel when the owner has not send the keys (i.e. status is PENDING)
    // NOTE: because the offer is deleted after canceling, this means the owner will not be able to accept it once it is canceled
    function cancelOffer(uint indexInOffersList) public returns (bool canceled){
        if (!isValidOfferMapping(indexInOffersList))
            return false;

        uint refundedValue = 0;
        Offer storage offer = offers[indexInOffersList];

        // buyer can only cancel when the owner has not send the keys
        if (offer.buyer == msg.sender &&
            offer.status == OfferStatus.PENDING &&
            offer.numEncryptedKeys == 0) {
            // delete it buy replace it with the last offer in the list (if any)
            refundedValue = offers[indexInOffersList].value;

            offer.status = OfferStatus.CANCELED;

            // refund if any
            if (0 < refundedValue) {
                msg.sender.transfer(refundedValue);
            }

            return true;
        }

        return false;
    }


    /// An owner sends keys
    // This can only happen before expiration time of an offer and still in PENDING status
    function sendKeys(uint indexInOffersList, bytes encryptedKeys_, uint numEncryptedKeys_) public returns (bool keySent) {
        if (!isValidOfferMapping(indexInOffersList))
            return false;

        Offer storage offer = offers[indexInOffersList];

        //if still not expired
        if (offer.owner == msg.sender &&
            offer.status == OfferStatus.PENDING &&
            block.timestamp < offer.offerExpiration) {
            offer.encryptedKeys = encryptedKeys_;
            offer.numEncryptedKeys = numEncryptedKeys_;
            offer.status = OfferStatus.KEYS_SUBMITTED;

            emit sendKeysEvent(msg.sender, offer.buyer, encryptedKeys_, numEncryptedKeys_);

            return true;
        }

        return false;
    }


    /// A owner withdraw payment from a buyer's offer after refund period and delete the offer
    function withdrawPayment(uint indexInOffersList) public returns (bool withdrawn){
        if (!isValidOfferMapping(indexInOffersList))
            return false;

        uint withdrawAmount = 0;
        Offer storage offer = offers[indexInOffersList];

        //if refund time expired
        if (offer.owner == msg.sender &&
            offer.status == OfferStatus.KEYS_SUBMITTED &&
            offer.refundExpiration < block.timestamp) {
            // delete this offer
            withdrawAmount = offer.value;

            offer.status = OfferStatus.WITHDRAWN;

            // withdraw if any
            if (0 < withdrawAmount) {
                msg.sender.transfer(withdrawAmount);
            }

            return true;
        }

        return false;
    }

    ///A buyer refunds
    function refundOffer(uint indexInOffersList) public returns (bool refunded){
        if (!isValidOfferMapping(indexInOffersList))
            return false;

        uint refundAmount = 0;
        Offer storage offer = offers[indexInOffersList];

        bool isRefundableStatus = (offer.status == OfferStatus.PENDING ||
            offer.status == OfferStatus.KEYS_SUBMITTED);

        //can only get refund after the offer expiration time and before refund expiration time
        if (offer.buyer == msg.sender &&
            isRefundableStatus &&
            offer.offerExpiration < block.timestamp &&
            block.timestamp < offer.refundExpiration) {
            refundAmount = offers[indexInOffersList].value;

            offer.status = OfferStatus.REFUNDED;

            // refundAmount if any
            if (0 < refundAmount) {
                msg.sender.transfer(refundAmount);
            }

            return true;
        }

        return false;
    }
}