package edu.usc.infolab.kien.blockchaingeospatial.contract.deployer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.tx.gas.DefaultGasProvider;

import edu.usc.infolab.kien.blockchaingeospatial.config.AddressConfig;
import edu.usc.infolab.kien.blockchaingeospatial.contract.generated.PrivGeoMarkt;
import edu.usc.infolab.kien.blockchaingeospatial.eth.EthHelper;

/**
 * Deploying PrivGeoMarkt contract
 */
public class PrivGeoMarktDeployer implements ContractDeployer {

    private static final Logger logger = LoggerFactory.getLogger(PrivGeoMarktDeployer.class);

    @Override
    public void deploy() throws Exception {
        logger.info("Starting...");

        logger.info("Initializing network components...");

        // We start by creating a new web3j instance to connect to remote nodes on the network.
        // Note: if using web3j Android, use Web3jFactory.build(...
        Web3j web3j = EthHelper.getWeb3jConnection();

        // We then need to load our Ethereum wallet file
        Credentials credentials = EthHelper.getCredentials();

        // Now lets deploy a smart contract
        logger.info("Deploying smart contract: PrivGeoMarkt");
//        PrivGeoMarkt contract = PrivGeoMarkt.deploy(web3j, credentials,new DefaultGasProvider(), AddressConfig.getAddress(AddressConfig.KEY_CONTRACT_PRIV_GEO_MARKT_STORAGE)).send();
        PrivGeoMarkt contract = PrivGeoMarkt.deploy(web3j, credentials,new DefaultGasProvider()).send();

        String contractAddress = contract.getContractAddress();
        logger.info("Smart contract PrivGeoMarkt deployed to address " + contractAddress);

        AddressConfig.saveAddress(AddressConfig.KEY_CONTRACT_PRIV_GEO_MARKT, contractAddress);

        logger.info("Address is saved to " + AddressConfig.ADDRESS_CONFIG_FILE);
        logger.info("Reload address = " + AddressConfig.getAddress(AddressConfig.KEY_CONTRACT_PRIV_GEO_MARKT));
    }
}
