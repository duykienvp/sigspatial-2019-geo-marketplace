# Java clients
The clients to interact with the smart contracts, which are developed in [smartcontract](/smartcontract) folder.
Internally, this client use [Web3j][web3j] to generate Java classes and interact with Ethereum blockchain.

## Requirements
- Java: tested on 1.8.0_51
- [Maven](https://maven.apache.org/): tested on 3.5.4
- [Web3j][web3j]: tested on 3.6.0

## Installation

### Generate Java classes from compiled Truffle json
In order to interact with the smart contracts on Ethereum blockchain, the Java adapters for those smart contracts must be created. 
Please refer to [Web3j documentation](https://docs.web3j.io/smart_contracts.html#smart-contract-wrappers) for more details. 
The following are general steps:
- Copy the smart contract json files compiled by Truffle in [smartcontract](/smartcontract) project to Web3j folder 
(e.g. from `build/contracts` in [smartcontract](/smartcontract) to `build/contracts` in Web3j folder)
- Generate Java adapters using Web3j binary. For example:
	- `bin/web3j truffle generate build/contracts/PrivGeoMarkt.json -o generated -p edu.usc.infolab.kien.blockchaingeospatial.contract.generated`
	- `bin/web3j truffle generate build/contracts/PrivGeoMarktStorage.json -o generated -p edu.usc.infolab.kien.blockchaingeospatial.contract.generated`
- Copy the generated Java classes to the corresponding package in Java (or Maven) project
- If the smart contracts were migrated by Truffle before this generation process, the generated Java classes may already contain the addresses of the smart contracts. 
However, one should not rely on these addresses because the smart contracts can also be deployed by other approaches.

### Generate executable JAR
After developing the project, we can create an executable JAR file using Maven command: `mvn clean install`. 
The executable JAR file, library folder `lib`, and the configuration folder `conf` are created in `target` folder.

## Usage
- Update configuration files in `conf` folder:
	- `config.properties`: main configuration file for network and credentials
	- `addresses.properties`: addresses on the blockchain for the smart contracts
	- `log4j2.properties`: [Log4j2](https://logging.apache.org/log4j/2.x/) configuration
- There are different app files in `edu.usc.infolab.kien.blockchaingeospatial.app` package 
which contain main method to run.  	
- Run the executable JAR file using command line. For example: `java -jar blockchain-geospatial-1.0.jar`

[web3j]: https://github.com/web3j/web3j
