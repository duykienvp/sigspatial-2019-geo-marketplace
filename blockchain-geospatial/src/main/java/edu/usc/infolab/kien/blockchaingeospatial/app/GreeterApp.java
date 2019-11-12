package edu.usc.infolab.kien.blockchaingeospatial.app;


import java.math.BigDecimal;

import edu.usc.infolab.kien.blockchaingeospatial.config.AddressConfig;
import edu.usc.infolab.kien.blockchaingeospatial.contract.generated.Greeter;
import edu.usc.infolab.kien.blockchaingeospatial.eth.EthHelper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tx.Transfer;
import org.web3j.tx.gas.DefaultGasProvider;
import org.web3j.utils.Convert;
import org.web3j.utils.Numeric;



/**
 * A simple web3j application that demonstrates a number of core features of
 * web3j:
 *
 * <ol>
 * <li>Connecting to a node on the Ethereum network</li>
 * <li>Loading an Ethereum wallet file</li>
 * <li>Sending Ether from one address to another</li>
 * <li>Deploying a smart contract to the network</li>
 * <li>Reading a value from the deployed smart contract</li>
 * <li>Updating a value in the deployed smart contract</li>
 * <li>Viewing an event logged by the smart contract</li>
 * </ol>
 *
 * <p>
 * To generateDataAndSubmitCommitment this demo, you will need to provide:
 *
 * <ol>
 * <li>Ethereum client (or node) endpoint. The simplest thing to do is
 * <a href="https://infura.io/register.html">request a free access token from
 * Infura</a></li>
 * <li>A wallet file. This can be generated using the web3j
 * <a href="https://docs.web3j.io/command_line.html">command line tools</a></li>
 * <li>Some Ether. This can be requested from the
 * <a href="https://www.rinkeby.io/#faucet">Rinkeby Faucet</a></li>
 * </ol>
 *
 * <p>
 * For further background information, refer to the project README.
 */
public class GreeterApp {

    private static final Logger log = LoggerFactory.getLogger(GreeterApp.class);

    public void run() throws Exception {
        log.info("Starting...");

        // We start by creating a new web3j instance to connect to remote nodes on the network.
        // Note: if using web3j Android, use Web3jFactory.build(...
        Web3j web3j = EthHelper.getWeb3jConnection();

        // We then need to load our Ethereum wallet file
        Credentials credentials = EthHelper.getCredentials();

        testSendFund(web3j, credentials);


        // Now lets deploy a smart contract

        Greeter contract = Greeter.load(
                AddressConfig.getAddress(AddressConfig.KEY_CONTRACT_GREETER),
                web3j,
                credentials,
                new DefaultGasProvider());

        log.info("Value stored in remote smart contract: " + contract.greet().send());

        // Lets modify the value in our smart contract
        TransactionReceipt transactionReceipt = contract.newGreeting("Well hello again again").send();

        log.info("New value stored in remote smart contract: " + contract.greet().send());

        // Events enable us to log specific events happening during the execution of our smart
        // contract to the blockchain. Index events cannot be logged in their entirety.
        // For Strings and arrays, the hash of values is provided, not the original value.
        // For further information, refer to https://docs.web3j.io/filters.html#filters-and-events
        for (Greeter.ModifiedEventResponse event : contract.getModifiedEvents(transactionReceipt)) {
            log.info("Modify event fired, previous value: " + event.oldGreeting
                    + ", new value: " + event.newGreeting);
            log.info("Indexed event previous value: " + Numeric.toHexString(event.oldGreetingIdx)
                    + ", new value: " + Numeric.toHexString(event.newGreetingIdx));
        }
    }

    /**
     * Test sending funds
     * @param web3j
     * @param credentials
     * @throws Exception
     */
    public void testSendFund(Web3j web3j, Credentials credentials) throws Exception {
        log.info("Sending 1 Wei (" + Convert.fromWei("1", Convert.Unit.ETHER).toPlainString() + " Ether)");

        String toAddress = "0x3de715086cd3628fc964d560d17388edfcc4e8d1";

        TransactionReceipt transferReceipt = Transfer.sendFunds(
                web3j,
                credentials,
                toAddress,
                BigDecimal.ONE,
                Convert.Unit.WEI)  //
                .send();

        log.info("Transaction complete, view it at https://rinkeby.etherscan.io/tx/"
                + transferReceipt.getTransactionHash());
    }
}
