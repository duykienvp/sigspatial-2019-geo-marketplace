#!/bin/sh
web3j-3.6.0/bin/web3j truffle generate ../smartcontract/build/contracts/PrivGeoMarkt.json -o web3j-3.6.0/generated -p edu.usc.infolab.kien.blockchaingeospatial.contract.generated
web3j-3.6.0/bin/web3j truffle generate ../smartcontract/build/contracts/PrivGeoMarktCommitmentStorage.json -o web3j-3.6.0/generated -p edu.usc.infolab.kien.blockchaingeospatial.contract.generated
web3j-3.6.0/bin/web3j truffle generate ../smartcontract/build/contracts/PrivGeoMarktSearchableStorage.json -o web3j-3.6.0/generated -p edu.usc.infolab.kien.blockchaingeospatial.contract.generated

cp web3j-3.6.0/generated/edu/usc/infolab/kien/blockchaingeospatial/contract/generated/PrivGeoMarkt.java ../blockchain-geospatial/src/main/java/edu/usc/infolab/kien/blockchaingeospatial/contract/generated/
cp web3j-3.6.0/generated/edu/usc/infolab/kien/blockchaingeospatial/contract/generated/PrivGeoMarktCommitmentStorage.java ../blockchain-geospatial/src/main/java/edu/usc/infolab/kien/blockchaingeospatial/contract/generated
cp web3j-3.6.0/generated/edu/usc/infolab/kien/blockchaingeospatial/contract/generated/PrivGeoMarktSearchableStorage.java ../blockchain-geospatial/src/main/java/edu/usc/infolab/kien/blockchaingeospatial/contract/generated
