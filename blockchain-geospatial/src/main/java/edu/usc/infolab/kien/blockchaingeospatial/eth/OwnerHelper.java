package edu.usc.infolab.kien.blockchaingeospatial.eth;

import edu.usc.infolab.kien.blockchaingeospatial.contract.generated.PrivGeoMarkt;
import edu.usc.infolab.kien.blockchaingeospatial.contract.generated.PrivGeoMarktCommitmentStorage;
import edu.usc.infolab.kien.blockchaingeospatial.monitor.TransactionMonitor;
import edu.usc.infolab.kien.blockchaingeospatial.monitor.TransactionMonitorMessage;
import edu.usc.infolab.kien.blockchaingeospatial.storage.CommitmentPublicParameters;
import edu.usc.infolab.kien.blockchaingeospatial.storage.PurchasePolicy;
import edu.usc.infolab.kien.blockchaingeospatial.utils.Utils;
import org.apache.commons.codec.binary.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tuples.generated.Tuple2;
import org.web3j.tuples.generated.Tuple5;
import org.web3j.tx.gas.ContractGasProvider;
import org.web3j.utils.Convert;

import java.math.BigDecimal;
import java.math.BigInteger;

public class OwnerHelper {
    private static final Logger logger = LoggerFactory.getLogger(OwnerHelper.class);

    /**
     * Create purchase policy of an owner
     * @param purchasePolicy
     * @param ownerPrivGeoMarkt
     * @param contractGasProvider
     * @return
     * @throws Exception
     */
    public static TransactionReceipt createPurchasePolicy(
        PurchasePolicy purchasePolicy,
        PrivGeoMarkt ownerPrivGeoMarkt,
        ContractGasProvider contractGasProvider) throws Exception {
        TransactionReceipt receipt = ownerPrivGeoMarkt
            .createPurchasePolicy(Convert.toWei(BigDecimal.valueOf(purchasePolicy.getMinPrice()), Convert.Unit.ETHER).toBigInteger())
            .send();
        TransactionMonitor.addMessage(
            new TransactionMonitorMessage(
                PrivGeoMarkt.FUNC_CREATEPURCHASEPOLICY,
                contractGasProvider.getGasPrice(PrivGeoMarkt.FUNC_CREATEPURCHASEPOLICY),
                receipt));

        return receipt;
    }

    /**
     * Get purchase policy of an owner
     * @param ownerAddress
     * @param ownerPrivGeoMarkt
     * @return PurchasePolicy or {@code null} if error occurred
     */
    public static PurchasePolicy getPurchasePolicyOfOwner(String ownerAddress, PrivGeoMarkt ownerPrivGeoMarkt) {
        PurchasePolicy purchasePolicy = null;
        try {
            BigInteger minPriceInt = ownerPrivGeoMarkt.getPurchasePolicyOfOwner(ownerAddress).send();
            BigDecimal minPrice = Convert.fromWei(BigDecimal.valueOf(minPriceInt.longValue()), Convert.Unit.ETHER);

            purchasePolicy = new PurchasePolicy();
            purchasePolicy.setMinPrice(minPrice.doubleValue());
        } catch (Exception e) {
            logger.error("Error getting purchase policy of " + ownerAddress);
        }

        return purchasePolicy;
    }

    /**
     * Submit commitment for data
     * @param ownerPrivGeoMarktCommitmentStorage
     * @param dataAddress
     * @param commitment
     * @param contractGasProvider
     * @return transaction receipt
     * @throws Exception
     */
    public static TransactionReceipt submitCommitment(
        PrivGeoMarktCommitmentStorage ownerPrivGeoMarktCommitmentStorage,
        String dataAddress,
        String commitment,
        ContractGasProvider contractGasProvider) throws Exception {
        TransactionReceipt receipt = ownerPrivGeoMarktCommitmentStorage.submitCommitment(
            Utils.hexStringToBytes(dataAddress), Utils.hexStringToBytes(commitment)).send();
        TransactionMonitor.addMessage(
            new TransactionMonitorMessage(
                PrivGeoMarktCommitmentStorage.FUNC_SUBMITCOMMITMENT,
                contractGasProvider.getGasPrice(PrivGeoMarktCommitmentStorage.FUNC_SUBMITCOMMITMENT),
                receipt));

        return receipt;
    }

    /**
     * Get commitment of a data address
     * @param ownerPrivGeoMarktCommitmentStorage
     * @param ownerAddress
     * @param dataAddress
     * @return
     * @throws Exception
     */
    public static String getCommitment(
        PrivGeoMarktCommitmentStorage ownerPrivGeoMarktCommitmentStorage,
        String ownerAddress,
        String dataAddress) throws Exception {
        Tuple2<byte[], BigInteger> commitmentTuple2 = ownerPrivGeoMarktCommitmentStorage.getCommitment(
            ownerAddress,
            Utils.hexStringToBytes(dataAddress)).send();
        String commitment = Hex.encodeHexString(commitmentTuple2.getValue1());
        logger.info("Commitments: commitment=" + commitment + " , modified= " + commitmentTuple2.getValue2());
        return commitment;
    }

    /**
     * Create commitment public parameters of an owner
     * @param commitmentPublicParameters
     * @param ownerPrivGeoMarktCommitmentStorage
     * @param contractGasProvider
     * @return
     * @throws Exception
     */
    public static TransactionReceipt createCommitmentPublicParameters(
        CommitmentPublicParameters commitmentPublicParameters,
        PrivGeoMarktCommitmentStorage ownerPrivGeoMarktCommitmentStorage,
        ContractGasProvider contractGasProvider) throws Exception {
        TransactionReceipt receipt = ownerPrivGeoMarktCommitmentStorage.createCommitmentPublicParameters(
            commitmentPublicParameters.getN(),
            commitmentPublicParameters.getA(),
            commitmentPublicParameters.getS(),
            commitmentPublicParameters.getC()).send();
        TransactionMonitor.addMessage(
            new TransactionMonitorMessage(
                PrivGeoMarktCommitmentStorage.FUNC_CREATECOMMITMENTPUBLICPARAMETERS,
                contractGasProvider.getGasPrice(PrivGeoMarktCommitmentStorage.FUNC_CREATECOMMITMENTPUBLICPARAMETERS),
                receipt));

        return receipt;
    }

    /**
     * Get commitment public parameters of an owner
     * @param ownerPrivGeoMarktCommitmentStorage
     * @param ownerAddress
     * @return CommitmentPublicParameters of this owner
     * @throws Exception
     */
    public static CommitmentPublicParameters getCommitmentPublicParameters(
        PrivGeoMarktCommitmentStorage ownerPrivGeoMarktCommitmentStorage,
        String ownerAddress) throws Exception {
        Tuple5<byte[], byte[], byte[], byte[], BigInteger> commitmentPPTuple5 =
            ownerPrivGeoMarktCommitmentStorage.getCommitmentPublicParameters(ownerAddress).send();
        CommitmentPublicParameters commitmentPublicParameters = new CommitmentPublicParameters();
        commitmentPublicParameters.setN(commitmentPPTuple5.getValue1());
        commitmentPublicParameters.setA(commitmentPPTuple5.getValue2());
        commitmentPublicParameters.setS(commitmentPPTuple5.getValue3());
        commitmentPublicParameters.setC(commitmentPPTuple5.getValue4());
        commitmentPublicParameters.setModifiedTime(commitmentPPTuple5.getValue5());

        return commitmentPublicParameters;
    }

    public static TransactionReceipt deleteCommitmentPublicParameters(
        PrivGeoMarktCommitmentStorage ownerPrivGeoMarktCommitmentStorage) throws Exception {
        TransactionReceipt receipt = ownerPrivGeoMarktCommitmentStorage.deleteCommitmentPublicParameter().send();
        return receipt;
    }
}
