pragma solidity ^0.4.24;

/// @title Private and Accountable Geospatial Market Place - Searchable Index Storage.
contract PrivGeoMarktSearchableStorage {

    constructor() public {
    }

    /********************************************** SEARCHABLE INDEX ******************************/
    /// A index address
    struct SearchableIndexInfo {
        address curator;
        bytes32 indexAddress;
        uint startTime;
        uint endTime;
        uint modifiedTime; //
        uint index; // index in the array searchableIndexInfosIndexMapping
    }

    // SearchableIndexInfo = SII
    // curator => mapping(indexAddress => SearchableIndexInfo)
    mapping(address => mapping(bytes32 => SearchableIndexInfo)) public searchableIndexInfos; // of each curatoe
    //list of index addresses of SII of each curator
    mapping(address => bytes32[]) public searchableIndexInfosIndexMapping;
    // index of a curator in the curatorIndices array
    mapping(address => uint) public curatorIndicesMapping;
    address[] public curatorIndices; //indices of curators committed
    //NOTE: we need to make sure the synchronization between the number of SearchableIndexInfos and the indices that indicate whether a curator has SearchableIndexInfo or not

    /// Get number of curator having searchable indices
    function getCuratorCount() public view returns (uint numCurator) {
        return curatorIndices.length;
    }

    function getNumSearchableIndexInfos(address curator) public view returns (uint numSearchableIndexInfos) {
        return searchableIndexInfosIndexMapping[curator].length;
    }

    function getSearchableIndexInfo(address curator, bytes32 indexAddress)
    public view
    returns (uint startTime, uint endTime, uint modifiedTime) {
        return (searchableIndexInfos[curator][indexAddress].startTime,
            searchableIndexInfos[curator][indexAddress].endTime,
            searchableIndexInfos[curator][indexAddress].modifiedTime);
    }

    /// Check whether a curator has SearchableIndexInfos or not
    function didCuratorHaveSearchableIndexInfos(address curator) public view returns(bool didHave)
    {
        if(curatorIndices.length == 0) return false;
        return (curatorIndices[curatorIndicesMapping[curator]] == curator);
    }

    /// Check if SearchableIndexInfos has data or not
    function didSearchableIndexInfoHaveData(address curator, bytes32 indexAddress) public view returns (bool didHave) {
        if (!didCuratorHaveSearchableIndexInfos(curator)) return false;
        if (searchableIndexInfosIndexMapping[curator].length == 0) return false;
        return (searchableIndexInfosIndexMapping[curator][searchableIndexInfos[curator][indexAddress].index] == indexAddress);
    }

    /// An curator submits a SearchableIndexInfos or update if it already existed. Current time will be recorded.
    /// Returns:
    ///   - whether this SearchableIndexInfos is successfully inserted or is updated
    function submitSearchableIndex(bytes32 indexAddress, uint startTime, uint endTime) public returns (bool isInserted) {
        address curator = msg.sender;
        if (didSearchableIndexInfoHaveData(curator, indexAddress)) {
            // SearchableIndexInfo already has data, update it
            searchableIndexInfos[curator][indexAddress].startTime = startTime;
            searchableIndexInfos[curator][indexAddress].endTime = endTime;
            searchableIndexInfos[curator][indexAddress].modifiedTime = now;
            isInserted = false;
        } else {
            //new SearchableIndexInfo, insert it
            searchableIndexInfos[curator][indexAddress].startTime = startTime;
            searchableIndexInfos[curator][indexAddress].endTime = endTime;
            searchableIndexInfos[curator][indexAddress].modifiedTime = now;
            searchableIndexInfos[curator][indexAddress].index = searchableIndexInfosIndexMapping[curator].push(indexAddress) - 1;
            isInserted = true;
        }

        if (!didCuratorHaveSearchableIndexInfos(curator)) {
            // not exist, insert this curator
            curatorIndicesMapping[curator] = curatorIndices.push(curator) - 1;
        }
    }

    /// An curator deletes a SearchableIndex for a data item
    function deleteSearchableIndex(bytes32 indexAddress) public returns (uint indexToDelete) {
        address curator = msg.sender;
        require(didSearchableIndexInfoHaveData(curator, indexAddress), "Searchable Index does not have data");
        //Delete
        //delete this row
        uint rowToDelete = searchableIndexInfos[curator][indexAddress].index;
        //this key will be used to replace the deleted row
        bytes32 keyToMove = searchableIndexInfosIndexMapping[curator][searchableIndexInfosIndexMapping[curator].length - 1];
        //now delete this row by replacing it with the key above
        searchableIndexInfosIndexMapping[curator][rowToDelete] = keyToMove;
        searchableIndexInfos[curator][keyToMove].index = rowToDelete;
        searchableIndexInfosIndexMapping[curator].length--;
        //delete the value in map

        delete searchableIndexInfos[curator][indexAddress];

        // if this curator does not have any searchable index, delete this curator
        if (searchableIndexInfosIndexMapping[curator].length == 0) {
            deleteCurator();
        }

        return rowToDelete;
    }

    /// A curator is deleted
    function deleteCurator() public returns (uint indexToDelete) {
        address curator = msg.sender;
        require(didCuratorHaveSearchableIndexInfos(curator), "Curator does not have searchable index infos");
        //delete this row
        uint rowToDelete = curatorIndicesMapping[curator];
        //this key will be used to replace the deleted row
        address keyToMove = curatorIndices[curatorIndices.length-1];
        //now delete this row by replacing it with the key above
        curatorIndicesMapping[keyToMove] = rowToDelete;
        curatorIndices[rowToDelete] = keyToMove;
        curatorIndices.length--;
        //delete value in map
        delete curatorIndicesMapping[curator];

        //delete all indices
        for (uint i = 0; i < searchableIndexInfosIndexMapping[curator].length; i++) {
            bytes32 indexAddr = searchableIndexInfosIndexMapping[curator][i];
            delete searchableIndexInfos[curator][indexAddr];
        }

        //delete mapping
        delete searchableIndexInfosIndexMapping[curator];

        return rowToDelete;
    }
}