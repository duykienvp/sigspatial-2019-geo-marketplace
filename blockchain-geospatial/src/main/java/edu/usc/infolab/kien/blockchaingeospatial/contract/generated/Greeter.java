package edu.usc.infolab.kien.blockchaingeospatial.contract.generated;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import org.web3j.abi.EventEncoder;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Event;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.response.Log;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tx.Contract;
import org.web3j.tx.TransactionManager;
import org.web3j.tx.gas.ContractGasProvider;
import rx.Observable;
import rx.functions.Func1;

/**
 * <p>Auto generated code.
 * <p><strong>Do not modify!</strong>
 * <p>Please use the <a href="https://docs.web3j.io/command_line.html">web3j command line tools</a>,
 * or the org.web3j.codegen.SolidityFunctionWrapperGenerator in the
 * <a href="https://github.com/web3j/web3j/tree/master/codegen">codegen module</a> to update.
 *
 * <p>Generated with web3j version 3.6.0.
 */
public class Greeter extends Contract {
    private static final String BINARY = "0x6060604052341561000f57600080fd5b6040516106073803806106078339810160405280805160008054600160a060020a03191633600160a060020a03161790559190910190506001818051610059929160200190610060565b50506100fb565b828054600181600116156101000203166002900490600052602060002090601f016020900481019282601f106100a157805160ff19168380011785556100ce565b828001600101855582156100ce579182015b828111156100ce5782518255916020019190600101906100b3565b506100da9291506100de565b5090565b6100f891905b808211156100da57600081556001016100e4565b90565b6104fd8061010a6000396000f3006060604052600436106100565763ffffffff7c010000000000000000000000000000000000000000000000000000000060003504166341c0e1b5811461005b5780634ac0d66e14610070578063cfae3217146100c1575b600080fd5b341561006657600080fd5b61006e61014b565b005b341561007b57600080fd5b61006e60046024813581810190830135806020601f8201819004810201604051908101604052818152929190602084018383808284375094965061018c95505050505050565b34156100cc57600080fd5b6100d461037e565b60405160208082528190810183818151815260200191508051906020019080838360005b838110156101105780820151838201526020016100f8565b50505050905090810190601f16801561013d5780820380516001836020036101000a031916815260200191505b509250505060405180910390f35b6000543373ffffffffffffffffffffffffffffffffffffffff9081169116141561018a5760005473ffffffffffffffffffffffffffffffffffffffff16ff5b565b806040518082805190602001908083835b602083106101bc5780518252601f19909201916020918201910161019d565b6001836020036101000a03801982511681845116179092525050509190910192506040915050518091039020600160405180828054600181600116156101000203166002900480156102455780601f10610223576101008083540402835291820191610245565b820191906000526020600020905b815481529060010190602001808311610231575b505091505060405180910390207f047dcd1aa8b77b0b943642129c767533eeacd700c7c1eab092b8ce05d2b2faf56001846040516040808252835460026000196101006001841615020190911604908201819052819060208201906060830190869080156102f45780601f106102c9576101008083540402835291602001916102f4565b820191906000526020600020905b8154815290600101906020018083116102d757829003601f168201915b5050838103825284818151815260200191508051906020019080838360005b8381101561032b578082015183820152602001610313565b50505050905090810190601f1680156103585780820380516001836020036101000a031916815260200191505b5094505050505060405180910390a3600181805161037a929160200190610427565b5050565b6103866104a5565b60018054600181600116156101000203166002900480601f01602080910402602001604051908101604052809291908181526020018280546001816001161561010002031660029004801561041c5780601f106103f15761010080835404028352916020019161041c565b820191906000526020600020905b8154815290600101906020018083116103ff57829003601f168201915b505050505090505b90565b828054600181600116156101000203166002900490600052602060002090601f016020900481019282601f1061046857805160ff1916838001178555610495565b82800160010185558215610495579182015b8281111561049557825182559160200191906001019061047a565b506104a19291506104b7565b5090565b60206040519081016040526000815290565b61042491905b808211156104a157600081556001016104bd5600a165627a7a72305820a63bea2874028f4d29c5ce25a171bcc3653065985d210de9978b24ed424e4a540029";

    public static final String FUNC_KILL = "kill";

    public static final String FUNC_NEWGREETING = "newGreeting";

    public static final String FUNC_GREET = "greet";

    public static final Event MODIFIED_EVENT = new Event("Modified",
            Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>(true) {}, new TypeReference<Utf8String>(true) {}, new TypeReference<Utf8String>() {}, new TypeReference<Utf8String>() {}));
    ;

    protected static final HashMap<String, String> _addresses;

    static {
        _addresses = new HashMap<String, String>();
    }

    @Deprecated
    protected Greeter(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    protected Greeter(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, credentials, contractGasProvider);
    }

    @Deprecated
    protected Greeter(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    protected Greeter(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public RemoteCall<TransactionReceipt> kill() {
        final Function function = new Function(
                FUNC_KILL,
                Arrays.<Type>asList(),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> newGreeting(String _greeting) {
        final Function function = new Function(
                FUNC_NEWGREETING,
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(_greeting)),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<String> greet() {
        final Function function = new Function(FUNC_GREET,
                Arrays.<Type>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public static RemoteCall<Greeter> deploy(Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider, String _greeting) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(_greeting)));
        return deployRemoteCall(Greeter.class, web3j, credentials, contractGasProvider, BINARY, encodedConstructor);
    }

    public static RemoteCall<Greeter> deploy(Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider, String _greeting) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(_greeting)));
        return deployRemoteCall(Greeter.class, web3j, transactionManager, contractGasProvider, BINARY, encodedConstructor);
    }

    @Deprecated
    public static RemoteCall<Greeter> deploy(Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit, String _greeting) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(_greeting)));
        return deployRemoteCall(Greeter.class, web3j, credentials, gasPrice, gasLimit, BINARY, encodedConstructor);
    }

    @Deprecated
    public static RemoteCall<Greeter> deploy(Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit, String _greeting) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(_greeting)));
        return deployRemoteCall(Greeter.class, web3j, transactionManager, gasPrice, gasLimit, BINARY, encodedConstructor);
    }

    public List<ModifiedEventResponse> getModifiedEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(MODIFIED_EVENT, transactionReceipt);
        ArrayList<ModifiedEventResponse> responses = new ArrayList<ModifiedEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            ModifiedEventResponse typedResponse = new ModifiedEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.oldGreetingIdx = (byte[]) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.newGreetingIdx = (byte[]) eventValues.getIndexedValues().get(1).getValue();
            typedResponse.oldGreeting = (String) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.newGreeting = (String) eventValues.getNonIndexedValues().get(1).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Observable<ModifiedEventResponse> modifiedEventObservable(EthFilter filter) {
        return web3j.ethLogObservable(filter).map(new Func1<Log, ModifiedEventResponse>() {
            @Override
            public ModifiedEventResponse call(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(MODIFIED_EVENT, log);
                ModifiedEventResponse typedResponse = new ModifiedEventResponse();
                typedResponse.log = log;
                typedResponse.oldGreetingIdx = (byte[]) eventValues.getIndexedValues().get(0).getValue();
                typedResponse.newGreetingIdx = (byte[]) eventValues.getIndexedValues().get(1).getValue();
                typedResponse.oldGreeting = (String) eventValues.getNonIndexedValues().get(0).getValue();
                typedResponse.newGreeting = (String) eventValues.getNonIndexedValues().get(1).getValue();
                return typedResponse;
            }
        });
    }

    public Observable<ModifiedEventResponse> modifiedEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(MODIFIED_EVENT));
        return modifiedEventObservable(filter);
    }

    @Deprecated
    public static Greeter load(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new Greeter(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    @Deprecated
    public static Greeter load(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new Greeter(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public static Greeter load(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        return new Greeter(contractAddress, web3j, credentials, contractGasProvider);
    }

    public static Greeter load(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return new Greeter(contractAddress, web3j, transactionManager, contractGasProvider);
    }

    protected String getStaticDeployedAddress(String networkId) {
        return _addresses.get(networkId);
    }

    public static String getPreviouslyDeployedAddress(String networkId) {
        return _addresses.get(networkId);
    }

    public static class ModifiedEventResponse {
        public Log log;

        public byte[] oldGreetingIdx;

        public byte[] newGreetingIdx;

        public String oldGreeting;

        public String newGreeting;
    }
}
