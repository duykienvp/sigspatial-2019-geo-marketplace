pragma solidity ^0.4.24;

/// @title Private and Accountable Geospatial Market Place Storage.
contract PrivGeoMarktCommitmentStorage {

    constructor() public {
    }

    /********************************************** COMMITMENTS ******************************/

    /// Commitment public parameters
    struct CommitmentPublicParameters {
        bytes N;
        bytes a;
        bytes s;
        bytes c;

        uint modifiedTime;
    }


    mapping(address => CommitmentPublicParameters) public commitmentPublicParameters; //CommitmentPublicParameters of each owner
    /// An owner creates a CommitmentPublicParameters for him.
    /// Update the current CommitmentPublicParameters if any.
    function createCommitmentPublicParameters(bytes N_, bytes a_, bytes s_, bytes c_) public {
        commitmentPublicParameters[msg.sender].N = N_;
        commitmentPublicParameters[msg.sender].a = a_;
        commitmentPublicParameters[msg.sender].s = s_;
        commitmentPublicParameters[msg.sender].c = c_;
        commitmentPublicParameters[msg.sender].modifiedTime = now;
    }

    function getCommitmentPublicParameters(address owner)
    public view
    returns (bytes N, bytes a, bytes s, bytes c, uint modifiedTime) {
        return (
        commitmentPublicParameters[owner].N,
        commitmentPublicParameters[owner].a,
        commitmentPublicParameters[owner].s,
        commitmentPublicParameters[owner].c,
        commitmentPublicParameters[owner].modifiedTime);
    }

    /// A user deletes his own CommitmentPublicParameters
    function deleteCommitmentPublicParameter() public {
        delete commitmentPublicParameters[msg.sender];
    }

    /// A single commitment
    struct Commitment {
        bytes commitment;  // digital commitment for this set of data
        uint modifiedTime; //
        uint index; //index in the lis
    }

    // owner => mapping(dataAddress => Commitment)
    mapping(address => mapping(bytes32 => Commitment)) public commitments; // commitments of each owner
    //list of data addresses of commitments  of each owner
    mapping(address => bytes32[]) public commitmentsIndicesMapping;
    mapping(address => uint) public ownerIndicesMapping; // index of a owner in the commitmentsIndices array
    address[] public ownerIndices; //indices of owners committed
    //NOTE: we need to make sure the synchonization between the number of commitments and the indices that indicate whether an owner has commitments or not

    /// Get number of owner having commitments
    function getOwnerCount() public view returns (uint numOnwer) {
        return ownerIndices.length;
    }

    function getNumCommitments(address owner) public view returns (uint numCommitments) {
        return commitmentsIndicesMapping[owner].length;
    }

    function getCommitment(address owner, bytes32 dataAddress)
    public view
    returns (bytes commitment, uint modifiedTime) {
        return (commitments[owner][dataAddress].commitment,
            commitments[owner][dataAddress].modifiedTime);
    }

    /// Check whether a user has commitments or not
    function didOwnerHaveCommitments(address owner) public view returns(bool didHave)
    {
        if(ownerIndices.length == 0) return false;
        return (ownerIndices[ownerIndicesMapping[owner]] == owner);
    }

    function didCommitmentHaveData(address owner, bytes32 dataAddress) public view returns (bool didHave) {
        if (!didOwnerHaveCommitments(owner)) return false;
        if (commitmentsIndicesMapping[owner].length == 0) return false;
        return (commitmentsIndicesMapping[owner][commitments[owner][dataAddress].index] == dataAddress);
    }

    /// An owner submits a commitment or update if it already existed. Current time will be recorded.
    /// Returns:
    ///   - whether this submission is successfully inserted or is updated
    function submitCommitment(bytes32 dataAddress, bytes commitment_) public returns (bool isInserted) {
        address owner = msg.sender;
        if (didCommitmentHaveData(owner, dataAddress)) {
            // commitment already has data, update it
            commitments[owner][dataAddress].commitment = commitment_;
            commitments[owner][dataAddress].modifiedTime = now;
            isInserted = false;
        } else {
            //new commitment, insert it
            commitments[owner][dataAddress].commitment = commitment_;
            commitments[owner][dataAddress].modifiedTime = now;
            commitments[owner][dataAddress].index = commitmentsIndicesMapping[owner].push(dataAddress) - 1;
            isInserted = true;
        }

        if (!didOwnerHaveCommitments(owner)) {
            // not exist, insert this owner
            ownerIndicesMapping[owner] = ownerIndices.push(owner) - 1;
        }
    }

    /// An owner deletes a commitment for a data item
    function deleteCommitment(bytes32 dataAddress) public returns (uint indexToDelete) {
        address owner = msg.sender;
        require(didCommitmentHaveData(owner, dataAddress), "Commitment does not have data");
        //Delete
        //delete this row
        uint rowToDelete = commitments[owner][dataAddress].index;
        //this key will be used to replace the deleted row
        bytes32 keyToMove = commitmentsIndicesMapping[owner][commitmentsIndicesMapping[owner].length - 1];
        //now delete this row by replacing it with the key above
        commitmentsIndicesMapping[owner][rowToDelete] = keyToMove;
        commitments[owner][keyToMove].index = rowToDelete;
        commitmentsIndicesMapping[owner].length--;
        //delete the value in map

        delete commitments[owner][dataAddress];

        // if this owner does not have any commitment, delete this owner
        if (commitmentsIndicesMapping[owner].length == 0) {
            deleteOwner();
        }

        return rowToDelete;
    }




    /// An owner (i.e. msg.sender) is deleted
    function deleteOwner() public returns (uint indexToDelete) {
        address owner = msg.sender;
        require(didOwnerHaveCommitments(owner), "Owner does not have commitments");
        //delete this row
        uint rowToDelete = ownerIndicesMapping[owner];
        //this key will be used to replace the deleted row
        address keyToMove = ownerIndices[ownerIndices.length-1];
        //now delete this row by replacing it with the key above
        ownerIndicesMapping[keyToMove] = rowToDelete;
        ownerIndices[rowToDelete] = keyToMove;
        ownerIndices.length--;
        //delete value in map
        delete ownerIndicesMapping[owner];

        //delete all commitments
        for (uint i = 0; i < commitmentsIndicesMapping[owner].length; i++) {
            bytes32 dataAddr = commitmentsIndicesMapping[owner][i];
            delete commitments[owner][dataAddr];
        }

        //delete mapping
        delete commitmentsIndicesMapping[owner];

        return rowToDelete;
    }
}