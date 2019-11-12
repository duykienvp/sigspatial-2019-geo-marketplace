package edu.usc.infolab.kien.blockchaingeospatial.contract.generated;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Callable;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Bool;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.generated.Bytes32;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tuples.generated.Tuple3;
import org.web3j.tuples.generated.Tuple6;
import org.web3j.tx.Contract;
import org.web3j.tx.TransactionManager;
import org.web3j.tx.gas.ContractGasProvider;

/**
 * <p>Auto generated code.
 * <p><strong>Do not modify!</strong>
 * <p>Please use the <a href="https://docs.web3j.io/command_line.html">web3j command line tools</a>,
 * or the org.web3j.codegen.SolidityFunctionWrapperGenerator in the 
 * <a href="https://github.com/web3j/web3j/tree/master/codegen">codegen module</a> to update.
 *
 * <p>Generated with web3j version 3.6.0.
 */
public class PrivGeoMarktSearchableStorage extends Contract {
    private static final String BINARY = "0x608060405234801561001057600080fd5b5061160e806100206000396000f3006080604052600436106100ba576000357c0100000000000000000000000000000000000000000000000000000000900463ffffffff1680630b006235146100bf57806323443bc01461011c578063363e8ddc14610185578063641755861461024157806364d541a4146102865780636910a8cc146102e1578063724a98df1461030c57806396f12e8714610363578063b2d68bde146103cc578063c097c07f14610423578063ee817f981461044e578063f04dbfcd146104bb575b600080fd5b3480156100cb57600080fd5b506101026004803603810190808035600019169060200190929190803590602001909291908035906020019092919050505061052e565b604051808215151515815260200191505060405180910390f35b34801561012857600080fd5b5061016b600480360381019080803573ffffffffffffffffffffffffffffffffffffffff169060200190929190803560001916906020019092919050505061091c565b604051808215151515815260200191505060405180910390f35b34801561019157600080fd5b506101d4600480360381019080803573ffffffffffffffffffffffffffffffffffffffff1690602001909291908035600019169060200190929190505050610a4e565b604051808773ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020018660001916600019168152602001858152602001848152602001838152602001828152602001965050505050505060405180910390f35b34801561024d57600080fd5b506102706004803603810190808035600019169060200190929190505050610ab7565b6040518082815260200191505060405180910390f35b34801561029257600080fd5b506102c7600480360381019080803573ffffffffffffffffffffffffffffffffffffffff169060200190929190505050610e78565b604051808215151515815260200191505060405180910390f35b3480156102ed57600080fd5b506102f6610f3f565b6040518082815260200191505060405180910390f35b34801561031857600080fd5b5061034d600480360381019080803573ffffffffffffffffffffffffffffffffffffffff16906020019092919050505061131d565b6040518082815260200191505060405180910390f35b34801561036f57600080fd5b506103ae600480360381019080803573ffffffffffffffffffffffffffffffffffffffff16906020019092919080359060200190929190505050611369565b60405180826000191660001916815260200191505060405180910390f35b3480156103d857600080fd5b5061040d600480360381019080803573ffffffffffffffffffffffffffffffffffffffff169060200190929190505050611399565b6040518082815260200191505060405180910390f35b34801561042f57600080fd5b506104386113b1565b6040518082815260200191505060405180910390f35b34801561045a57600080fd5b50610479600480360381019080803590602001909291905050506113be565b604051808273ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200191505060405180910390f35b3480156104c757600080fd5b5061050a600480360381019080803573ffffffffffffffffffffffffffffffffffffffff16906020019092919080356000191690602001909291905050506113fc565b60405180848152602001838152602001828152602001935050505060405180910390f35b60008033905061053e818661091c565b1561066957836000808373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020019081526020016000206000876000191660001916815260200190815260200160002060020181905550826000808373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020019081526020016000206000876000191660001916815260200190815260200160002060030181905550426000808373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001908152602001600020600087600019166000191681526020019081526020016000206004018190555060009150610859565b836000808373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020019081526020016000206000876000191660001916815260200190815260200160002060020181905550826000808373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020019081526020016000206000876000191660001916815260200190815260200160002060030181905550426000808373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001908152602001600020600087600019166000191681526020019081526020016000206004018190555060018060008373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020019081526020016000208690806001815401808255809150509060018203906000526020600020016000909192909190915090600019169055036000808373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020019081526020016000206000876000191660001916815260200190815260200160002060050181905550600191505b61086281610e78565b151561091457600160038290806001815401808255809150509060018203906000526020600020016000909192909190916101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff16021790555003600260008373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001908152602001600020819055505b509392505050565b600061092783610e78565b15156109365760009050610a48565b6000600160008573ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002080549050141561098a5760009050610a48565b8160001916600160008573ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020019081526020016000206000808673ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020019081526020016000206000856000191660001916815260200190815260200160002060050154815481101515610a3557fe5b9060005260206000200154600019161490505b92915050565b6000602052816000526040600020602052806000526040600020600091509150508060000160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff16908060010154908060020154908060030154908060040154908060050154905086565b600080600080339250610aca838661091c565b1515610b64576040517f08c379a00000000000000000000000000000000000000000000000000000000081526004018080602001828103825260238152602001807f53656172636861626c6520496e64657820646f6573206e6f742068617665206481526020017f617461000000000000000000000000000000000000000000000000000000000081525060400191505060405180910390fd5b6000808473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060008660001916600019168152602001908152602001600020600501549150600160008473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060018060008673ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020019081526020016000208054905003815481101515610c5157fe5b9060005260206000200154905080600160008573ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002083815481101515610cab57fe5b906000526020600020018160001916905550816000808573ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020019081526020016000206000836000191660001916815260200190815260200160002060050181905550600160008473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001908152602001600020805480919060019003610d6e919061151f565b506000808473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060008660001916600019168152602001908152602001600020600080820160006101000a81549073ffffffffffffffffffffffffffffffffffffffff02191690556001820160009055600282016000905560038201600090556004820160009055600582016000905550506000600160008573ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001908152602001600020805490501415610e6d57610e6b610f3f565b505b819350505050919050565b6000806003805490501415610e905760009050610f3a565b8173ffffffffffffffffffffffffffffffffffffffff166003600260008573ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002054815481101515610ef557fe5b9060005260206000200160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff161490505b919050565b600080600080600080339450610f5485610e78565b1515610fee576040517f08c379a000000000000000000000000000000000000000000000000000000000815260040180806020018281038252602c8152602001807f43757261746f7220646f6573206e6f7420686176652073656172636861626c6581526020017f20696e64657820696e666f73000000000000000000000000000000000000000081525060400191505060405180910390fd5b600260008673ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001908152602001600020549350600360016003805490500381548110151561104757fe5b9060005260206000200160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff16925083600260008573ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002081905550826003858154811015156110c857fe5b9060005260206000200160006101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff1602179055506003805480919060019003611125919061154b565b50600260008673ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060009055600091505b600160008673ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001908152602001600020805490508210156112c757600160008673ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020019081526020016000208281548110151561120457fe5b906000526020600020015490506000808673ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060008260001916600019168152602001908152602001600020600080820160006101000a81549073ffffffffffffffffffffffffffffffffffffffff0219169055600182016000905560028201600090556003820160009055600482016000905560058201600090555050818060010192505061116e565b600160008673ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060006113129190611577565b839550505050505090565b6000600160008373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001908152602001600020805490509050919050565b60016020528160005260406000208181548110151561138457fe5b90600052602060002001600091509150505481565b60026020528060005260406000206000915090505481565b6000600380549050905090565b6003818154811015156113cd57fe5b906000526020600020016000915054906101000a900473ffffffffffffffffffffffffffffffffffffffff1681565b60008060008060008673ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060008560001916600019168152602001908152602001600020600201546000808773ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060008660001916600019168152602001908152602001600020600301546000808873ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060008760001916600019168152602001908152602001600020600401549250925092509250925092565b815481835581811115611546578183600052602060002091820191016115459190611598565b5b505050565b8154818355818111156115725781836000526020600020918201910161157191906115bd565b5b505050565b50805460008255906000526020600020908101906115959190611598565b50565b6115ba91905b808211156115b657600081600090555060010161159e565b5090565b90565b6115df91905b808211156115db5760008160009055506001016115c3565b5090565b905600a165627a7a7230582032e8911cb9a5c4b153cff16e713cfb0bec3698d93e06d46b6c0f8d1c12d958e40029";

    public static final String FUNC_SEARCHABLEINDEXINFOS = "searchableIndexInfos";

    public static final String FUNC_SEARCHABLEINDEXINFOSINDEXMAPPING = "searchableIndexInfosIndexMapping";

    public static final String FUNC_CURATORINDICESMAPPING = "curatorIndicesMapping";

    public static final String FUNC_CURATORINDICES = "curatorIndices";

    public static final String FUNC_GETCURATORCOUNT = "getCuratorCount";

    public static final String FUNC_GETNUMSEARCHABLEINDEXINFOS = "getNumSearchableIndexInfos";

    public static final String FUNC_GETSEARCHABLEINDEXINFO = "getSearchableIndexInfo";

    public static final String FUNC_DIDCURATORHAVESEARCHABLEINDEXINFOS = "didCuratorHaveSearchableIndexInfos";

    public static final String FUNC_DIDSEARCHABLEINDEXINFOHAVEDATA = "didSearchableIndexInfoHaveData";

    public static final String FUNC_SUBMITSEARCHABLEINDEX = "submitSearchableIndex";

    public static final String FUNC_DELETESEARCHABLEINDEX = "deleteSearchableIndex";

    public static final String FUNC_DELETECURATOR = "deleteCurator";

    protected static final HashMap<String, String> _addresses;

    static {
        _addresses = new HashMap<String, String>();
        _addresses.put("1", "0xfe3ba6f26bfdbe61b2e69568e277d14c8266134f");
    }

    @Deprecated
    protected PrivGeoMarktSearchableStorage(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    protected PrivGeoMarktSearchableStorage(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, credentials, contractGasProvider);
    }

    @Deprecated
    protected PrivGeoMarktSearchableStorage(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    protected PrivGeoMarktSearchableStorage(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public RemoteCall<Tuple6<String, byte[], BigInteger, BigInteger, BigInteger, BigInteger>> searchableIndexInfos(String param0, byte[] param1) {
        final Function function = new Function(FUNC_SEARCHABLEINDEXINFOS, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(param0), 
                new org.web3j.abi.datatypes.generated.Bytes32(param1)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}, new TypeReference<Bytes32>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}));
        return new RemoteCall<Tuple6<String, byte[], BigInteger, BigInteger, BigInteger, BigInteger>>(
                new Callable<Tuple6<String, byte[], BigInteger, BigInteger, BigInteger, BigInteger>>() {
                    @Override
                    public Tuple6<String, byte[], BigInteger, BigInteger, BigInteger, BigInteger> call() throws Exception {
                        List<Type> results = executeCallMultipleValueReturn(function);
                        return new Tuple6<String, byte[], BigInteger, BigInteger, BigInteger, BigInteger>(
                                (String) results.get(0).getValue(), 
                                (byte[]) results.get(1).getValue(), 
                                (BigInteger) results.get(2).getValue(), 
                                (BigInteger) results.get(3).getValue(), 
                                (BigInteger) results.get(4).getValue(), 
                                (BigInteger) results.get(5).getValue());
                    }
                });
    }

    public RemoteCall<byte[]> searchableIndexInfosIndexMapping(String param0, BigInteger param1) {
        final Function function = new Function(FUNC_SEARCHABLEINDEXINFOSINDEXMAPPING, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(param0), 
                new org.web3j.abi.datatypes.generated.Uint256(param1)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bytes32>() {}));
        return executeRemoteCallSingleValueReturn(function, byte[].class);
    }

    public RemoteCall<BigInteger> curatorIndicesMapping(String param0) {
        final Function function = new Function(FUNC_CURATORINDICESMAPPING, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(param0)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<String> curatorIndices(BigInteger param0) {
        final Function function = new Function(FUNC_CURATORINDICES, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(param0)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public static RemoteCall<PrivGeoMarktSearchableStorage> deploy(Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        return deployRemoteCall(PrivGeoMarktSearchableStorage.class, web3j, credentials, contractGasProvider, BINARY, "");
    }

    public static RemoteCall<PrivGeoMarktSearchableStorage> deploy(Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return deployRemoteCall(PrivGeoMarktSearchableStorage.class, web3j, transactionManager, contractGasProvider, BINARY, "");
    }

    @Deprecated
    public static RemoteCall<PrivGeoMarktSearchableStorage> deploy(Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(PrivGeoMarktSearchableStorage.class, web3j, credentials, gasPrice, gasLimit, BINARY, "");
    }

    @Deprecated
    public static RemoteCall<PrivGeoMarktSearchableStorage> deploy(Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(PrivGeoMarktSearchableStorage.class, web3j, transactionManager, gasPrice, gasLimit, BINARY, "");
    }

    public RemoteCall<BigInteger> getCuratorCount() {
        final Function function = new Function(FUNC_GETCURATORCOUNT, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<BigInteger> getNumSearchableIndexInfos(String curator) {
        final Function function = new Function(FUNC_GETNUMSEARCHABLEINDEXINFOS, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(curator)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<Tuple3<BigInteger, BigInteger, BigInteger>> getSearchableIndexInfo(String curator, byte[] indexAddress) {
        final Function function = new Function(FUNC_GETSEARCHABLEINDEXINFO, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(curator), 
                new org.web3j.abi.datatypes.generated.Bytes32(indexAddress)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}));
        return new RemoteCall<Tuple3<BigInteger, BigInteger, BigInteger>>(
                new Callable<Tuple3<BigInteger, BigInteger, BigInteger>>() {
                    @Override
                    public Tuple3<BigInteger, BigInteger, BigInteger> call() throws Exception {
                        List<Type> results = executeCallMultipleValueReturn(function);
                        return new Tuple3<BigInteger, BigInteger, BigInteger>(
                                (BigInteger) results.get(0).getValue(), 
                                (BigInteger) results.get(1).getValue(), 
                                (BigInteger) results.get(2).getValue());
                    }
                });
    }

    public RemoteCall<Boolean> didCuratorHaveSearchableIndexInfos(String curator) {
        final Function function = new Function(FUNC_DIDCURATORHAVESEARCHABLEINDEXINFOS, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(curator)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return executeRemoteCallSingleValueReturn(function, Boolean.class);
    }

    public RemoteCall<Boolean> didSearchableIndexInfoHaveData(String curator, byte[] indexAddress) {
        final Function function = new Function(FUNC_DIDSEARCHABLEINDEXINFOHAVEDATA, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(curator), 
                new org.web3j.abi.datatypes.generated.Bytes32(indexAddress)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return executeRemoteCallSingleValueReturn(function, Boolean.class);
    }

    public RemoteCall<TransactionReceipt> submitSearchableIndex(byte[] indexAddress, BigInteger startTime, BigInteger endTime) {
        final Function function = new Function(
                FUNC_SUBMITSEARCHABLEINDEX, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(indexAddress), 
                new org.web3j.abi.datatypes.generated.Uint256(startTime), 
                new org.web3j.abi.datatypes.generated.Uint256(endTime)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> deleteSearchableIndex(byte[] indexAddress) {
        final Function function = new Function(
                FUNC_DELETESEARCHABLEINDEX, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(indexAddress)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> deleteCurator() {
        final Function function = new Function(
                FUNC_DELETECURATOR, 
                Arrays.<Type>asList(), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    @Deprecated
    public static PrivGeoMarktSearchableStorage load(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new PrivGeoMarktSearchableStorage(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    @Deprecated
    public static PrivGeoMarktSearchableStorage load(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new PrivGeoMarktSearchableStorage(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public static PrivGeoMarktSearchableStorage load(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        return new PrivGeoMarktSearchableStorage(contractAddress, web3j, credentials, contractGasProvider);
    }

    public static PrivGeoMarktSearchableStorage load(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return new PrivGeoMarktSearchableStorage(contractAddress, web3j, transactionManager, contractGasProvider);
    }

    protected String getStaticDeployedAddress(String networkId) {
        return _addresses.get(networkId);
    }

    public static String getPreviouslyDeployedAddress(String networkId) {
        return _addresses.get(networkId);
    }
}
