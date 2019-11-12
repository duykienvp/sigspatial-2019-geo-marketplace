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
import org.web3j.abi.datatypes.DynamicBytes;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.generated.Bytes32;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tuples.generated.Tuple2;
import org.web3j.tuples.generated.Tuple3;
import org.web3j.tuples.generated.Tuple5;
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
public class PrivGeoMarktCommitmentStorage extends Contract {
    private static final String BINARY = "0x608060405234801561001057600080fd5b5061250c806100206000396000f3006080604052600436106100e6576000357c0100000000000000000000000000000000000000000000000000000000900463ffffffff1680630feb209b146100eb57806322550a42146102f2578063317e4dcc146103c3578063377eb029146104fe5780633bae15df1461056b57806356a795251461059657806371b7dbbc146105ed5780637d48c25a1461064857806380bb0e251461069f57806385b214cf1461072e5780638a7ffa00146107735780638b1592b51461078a57806391f2d68d14610862578063a8094303146108cb578063be859f7514610ad2578063ef18374a14610b3b575b600080fd5b3480156100f757600080fd5b5061012c600480360381019080803573ffffffffffffffffffffffffffffffffffffffff169060200190929190505050610b66565b604051808060200180602001806020018060200186815260200185810385528a818151815260200191508051906020019080838360005b8381101561017e578082015181840152602081019050610163565b50505050905090810190601f1680156101ab5780820380516001836020036101000a031916815260200191505b50858103845289818151815260200191508051906020019080838360005b838110156101e45780820151818401526020810190506101c9565b50505050905090810190601f1680156102115780820380516001836020036101000a031916815260200191505b50858103835288818151815260200191508051906020019080838360005b8381101561024a57808201518184015260208101905061022f565b50505050905090810190601f1680156102775780820380516001836020036101000a031916815260200191505b50858103825287818151815260200191508051906020019080838360005b838110156102b0578082015181840152602081019050610295565b50505050905090810190601f1680156102dd5780820380516001836020036101000a031916815260200191505b50995050505050505050505060405180910390f35b3480156102fe57600080fd5b50610341600480360381019080803573ffffffffffffffffffffffffffffffffffffffff1690602001909291908035600019169060200190929190505050610dfc565b6040518080602001838152602001828103825284818151815260200191508051906020019080838360005b8381101561038757808201518184015260208101905061036c565b50505050905090810190601f1680156103b45780820380516001836020036101000a031916815260200191505b50935050505060405180910390f35b3480156103cf57600080fd5b506104fc600480360381019080803590602001908201803590602001908080601f0160208091040260200160405190810160405280939291908181526020018383808284378201915050505050509192919290803590602001908201803590602001908080601f0160208091040260200160405190810160405280939291908181526020018383808284378201915050505050509192919290803590602001908201803590602001908080601f0160208091040260200160405190810160405280939291908181526020018383808284378201915050505050509192919290803590602001908201803590602001908080601f0160208091040260200160405190810160405280939291908181526020018383808284378201915050505050509192919290505050610f5e565b005b34801561050a57600080fd5b5061052960048036038101908080359060200190929190505050611102565b604051808273ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200191505060405180910390f35b34801561057757600080fd5b50610580611140565b6040518082815260200191505060405180910390f35b3480156105a257600080fd5b506105d7600480360381019080803573ffffffffffffffffffffffffffffffffffffffff1690602001909291905050506114ca565b6040518082815260200191505060405180910390f35b3480156105f957600080fd5b5061062e600480360381019080803573ffffffffffffffffffffffffffffffffffffffff169060200190929190505050611516565b604051808215151515815260200191505060405180910390f35b34801561065457600080fd5b50610689600480360381019080803573ffffffffffffffffffffffffffffffffffffffff1690602001909291905050506115dd565b6040518082815260200191505060405180910390f35b3480156106ab57600080fd5b506107146004803603810190808035600019169060200190929190803590602001908201803590602001908080601f01602080910402602001604051908101604052809392919081815260200183838082843782019150505050505091929192905050506115f5565b604051808215151515815260200191505060405180910390f35b34801561073a57600080fd5b5061075d600480360381019080803560001916906020019092919050505061194a565b6040518082815260200191505060405180910390f35b34801561077f57600080fd5b50610788611cba565b005b34801561079657600080fd5b506107d9600480360381019080803573ffffffffffffffffffffffffffffffffffffffff1690602001909291908035600019169060200190929190505050611d45565b6040518080602001848152602001838152602001828103825285818151815260200191508051906020019080838360005b8381101561082557808201518184015260208101905061080a565b50505050905090810190601f1680156108525780820380516001836020036101000a031916815260200191505b5094505050505060405180910390f35b34801561086e57600080fd5b506108b1600480360381019080803573ffffffffffffffffffffffffffffffffffffffff1690602001909291908035600019169060200190929190505050611e14565b604051808215151515815260200191505060405180910390f35b3480156108d757600080fd5b5061090c600480360381019080803573ffffffffffffffffffffffffffffffffffffffff169060200190929190505050611f47565b604051808060200180602001806020018060200186815260200185810385528a818151815260200191508051906020019080838360005b8381101561095e578082015181840152602081019050610943565b50505050905090810190601f16801561098b5780820380516001836020036101000a031916815260200191505b50858103845289818151815260200191508051906020019080838360005b838110156109c45780820151818401526020810190506109a9565b50505050905090810190601f1680156109f15780820380516001836020036101000a031916815260200191505b50858103835288818151815260200191508051906020019080838360005b83811015610a2a578082015181840152602081019050610a0f565b50505050905090810190601f168015610a575780820380516001836020036101000a031916815260200191505b50858103825287818151815260200191508051906020019080838360005b83811015610a90578082015181840152602081019050610a75565b50505050905090810190601f168015610abd5780820380516001836020036101000a031916815260200191505b50995050505050505050505060405180910390f35b348015610ade57600080fd5b50610b1d600480360381019080803573ffffffffffffffffffffffffffffffffffffffff16906020019092919080359060200190929190505050612318565b60405180826000191660001916815260200191505060405180910390f35b348015610b4757600080fd5b50610b50612348565b6040518082815260200191505060405180910390f35b6000602052806000526040600020600091509050806000018054600181600116156101000203166002900480601f016020809104026020016040519081016040528092919081815260200182805460018160011615610100020316600290048015610c125780601f10610be757610100808354040283529160200191610c12565b820191906000526020600020905b815481529060010190602001808311610bf557829003601f168201915b505050505090806001018054600181600116156101000203166002900480601f016020809104026020016040519081016040528092919081815260200182805460018160011615610100020316600290048015610cb05780601f10610c8557610100808354040283529160200191610cb0565b820191906000526020600020905b815481529060010190602001808311610c9357829003601f168201915b505050505090806002018054600181600116156101000203166002900480601f016020809104026020016040519081016040528092919081815260200182805460018160011615610100020316600290048015610d4e5780601f10610d2357610100808354040283529160200191610d4e565b820191906000526020600020905b815481529060010190602001808311610d3157829003601f168201915b505050505090806003018054600181600116156101000203166002900480601f016020809104026020016040519081016040528092919081815260200182805460018160011615610100020316600290048015610dec5780601f10610dc157610100808354040283529160200191610dec565b820191906000526020600020905b815481529060010190602001808311610dcf57829003601f168201915b5050505050908060040154905085565b60606000600160008573ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060008460001916600019168152602001908152602001600020600001600160008673ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020019081526020016000206000856000191660001916815260200190815260200160002060010154818054600181600116156101000203166002900480601f016020809104026020016040519081016040528092919081815260200182805460018160011615610100020316600290048015610f4c5780601f10610f2157610100808354040283529160200191610f4c565b820191906000526020600020905b815481529060010190602001808311610f2f57829003601f168201915b50505050509150915091509250929050565b836000803373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020019081526020016000206000019080519060200190610fb3929190612355565b50826000803373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020019081526020016000206001019080519060200190611009929190612355565b50816000803373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001908152602001600020600201908051906020019061105f929190612355565b50806000803373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060030190805190602001906110b5929190612355565b50426000803373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020019081526020016000206004018190555050505050565b60048181548110151561111157fe5b906000526020600020016000915054906101000a900473ffffffffffffffffffffffffffffffffffffffff1681565b60008060008060008033945061115585611516565b15156111c9576040517f08c379a000000000000000000000000000000000000000000000000000000000815260040180806020018281038252601f8152602001807f4f776e657220646f6573206e6f74206861766520636f6d6d69746d656e74730081525060200191505060405180910390fd5b600360008673ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001908152602001600020549350600460016004805490500381548110151561122257fe5b9060005260206000200160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff16925083600360008573ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002081905550826004858154811015156112a357fe5b9060005260206000200160006101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff160217905550600480548091906001900361130091906123d5565b50600360008673ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060009055600091505b600260008673ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020019081526020016000208054905082101561147457600260008673ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001908152602001600020828154811015156113df57fe5b90600052602060002001549050600160008673ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060008260001916600019168152602001908152602001600020600080820160006114559190612401565b6001820160009055600282016000905550508180600101925050611349565b600260008673ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060006114bf9190612449565b839550505050505090565b6000600260008373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001908152602001600020805490509050919050565b600080600480549050141561152e57600090506115d8565b8173ffffffffffffffffffffffffffffffffffffffff166004600360008573ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020019081526020016000205481548110151561159357fe5b9060005260206000200160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff161490505b919050565b60036020528060005260406000206000915090505481565b6000803390506116058185611e14565b156116e35782600160008373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001908152602001600020600086600019166000191681526020019081526020016000206000019080519060200190611679929190612355565b5042600160008373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001908152602001600020600086600019166000191681526020019081526020016000206001018190555060009150611888565b82600160008373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001908152602001600020600086600019166000191681526020019081526020016000206000019080519060200190611752929190612355565b5042600160008373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060008660001916600019168152602001908152602001600020600101819055506001600260008373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001908152602001600020859080600181540180825580915050906001820390600052602060002001600090919290919091509060001916905503600160008373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020019081526020016000206000866000191660001916815260200190815260200160002060020181905550600191505b61189181611516565b151561194357600160048290806001815401808255809150509060018203906000526020600020016000909192909190916101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff16021790555003600360008373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001908152602001600020819055505b5092915050565b60008060008033925061195d8386611e14565b15156119d1576040517f08c379a000000000000000000000000000000000000000000000000000000000815260040180806020018281038252601d8152602001807f436f6d6d69746d656e7420646f6573206e6f742068617665206461746100000081525060200191505060405180910390fd5b600160008473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060008660001916600019168152602001908152602001600020600201549150600260008473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020019081526020016000206001600260008673ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020019081526020016000208054905003815481101515611ac057fe5b9060005260206000200154905080600260008573ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002083815481101515611b1a57fe5b90600052602060002001816000191690555081600160008573ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020019081526020016000206000836000191660001916815260200190815260200160002060020181905550600260008473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001908152602001600020805480919060019003611bde919061246a565b50600160008473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020019081526020016000206000866000191660001916815260200190815260200160002060008082016000611c489190612401565b6001820160009055600282016000905550506000600260008573ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001908152602001600020805490501415611caf57611cad611140565b505b819350505050919050565b6000803373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060008082016000611d099190612401565b600182016000611d199190612401565b600282016000611d299190612401565b600382016000611d399190612401565b60048201600090555050565b600160205281600052604060002060205280600052604060002060009150915050806000018054600181600116156101000203166002900480601f016020809104026020016040519081016040528092919081815260200182805460018160011615610100020316600290048015611dfe5780601f10611dd357610100808354040283529160200191611dfe565b820191906000526020600020905b815481529060010190602001808311611de157829003601f168201915b5050505050908060010154908060020154905083565b6000611e1f83611516565b1515611e2e5760009050611f41565b6000600260008573ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001908152602001600020805490501415611e825760009050611f41565b8160001916600260008573ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001908152602001600020600160008673ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020019081526020016000206000856000191660001916815260200190815260200160002060020154815481101515611f2e57fe5b9060005260206000200154600019161490505b92915050565b60608060608060008060008773ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020019081526020016000206000016000808873ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020019081526020016000206001016000808973ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020019081526020016000206002016000808a73ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020019081526020016000206003016000808b73ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060040154848054600181600116156101000203166002900480601f01602080910402602001604051908101604052809291908181526020018280546001816001161561010002031660029004801561212a5780601f106120ff5761010080835404028352916020019161212a565b820191906000526020600020905b81548152906001019060200180831161210d57829003601f168201915b50505050509450838054600181600116156101000203166002900480601f0160208091040260200160405190810160405280929190818152602001828054600181600116156101000203166002900480156121c65780601f1061219b576101008083540402835291602001916121c6565b820191906000526020600020905b8154815290600101906020018083116121a957829003601f168201915b50505050509350828054600181600116156101000203166002900480601f0160208091040260200160405190810160405280929190818152602001828054600181600116156101000203166002900480156122625780601f1061223757610100808354040283529160200191612262565b820191906000526020600020905b81548152906001019060200180831161224557829003601f168201915b50505050509250818054600181600116156101000203166002900480601f0160208091040260200160405190810160405280929190818152602001828054600181600116156101000203166002900480156122fe5780601f106122d3576101008083540402835291602001916122fe565b820191906000526020600020905b8154815290600101906020018083116122e157829003601f168201915b505050505091509450945094509450945091939590929450565b60026020528160005260406000208181548110151561233357fe5b90600052602060002001600091509150505481565b6000600480549050905090565b828054600181600116156101000203166002900490600052602060002090601f016020900481019282601f1061239657805160ff19168380011785556123c4565b828001600101855582156123c4579182015b828111156123c35782518255916020019190600101906123a8565b5b5090506123d19190612496565b5090565b8154818355818111156123fc578183600052602060002091820191016123fb9190612496565b5b505050565b50805460018160011615610100020316600290046000825580601f106124275750612446565b601f0160209004906000526020600020908101906124459190612496565b5b50565b508054600082559060005260206000209081019061246791906124bb565b50565b8154818355818111156124915781836000526020600020918201910161249091906124bb565b5b505050565b6124b891905b808211156124b457600081600090555060010161249c565b5090565b90565b6124dd91905b808211156124d95760008160009055506001016124c1565b5090565b905600a165627a7a72305820aaa556cac80fc0ddd9972074a9f882824c235648f8cd5045de08609b0df22de70029";

    public static final String FUNC_COMMITMENTPUBLICPARAMETERS = "commitmentPublicParameters";

    public static final String FUNC_OWNERINDICES = "ownerIndices";

    public static final String FUNC_OWNERINDICESMAPPING = "ownerIndicesMapping";

    public static final String FUNC_COMMITMENTS = "commitments";

    public static final String FUNC_COMMITMENTSINDICESMAPPING = "commitmentsIndicesMapping";

    public static final String FUNC_CREATECOMMITMENTPUBLICPARAMETERS = "createCommitmentPublicParameters";

    public static final String FUNC_GETCOMMITMENTPUBLICPARAMETERS = "getCommitmentPublicParameters";

    public static final String FUNC_DELETECOMMITMENTPUBLICPARAMETER = "deleteCommitmentPublicParameter";

    public static final String FUNC_GETOWNERCOUNT = "getOwnerCount";

    public static final String FUNC_GETNUMCOMMITMENTS = "getNumCommitments";

    public static final String FUNC_GETCOMMITMENT = "getCommitment";

    public static final String FUNC_DIDOWNERHAVECOMMITMENTS = "didOwnerHaveCommitments";

    public static final String FUNC_DIDCOMMITMENTHAVEDATA = "didCommitmentHaveData";

    public static final String FUNC_SUBMITCOMMITMENT = "submitCommitment";

    public static final String FUNC_DELETECOMMITMENT = "deleteCommitment";

    public static final String FUNC_DELETEOWNER = "deleteOwner";

    protected static final HashMap<String, String> _addresses;

    static {
        _addresses = new HashMap<String, String>();
        _addresses.put("1", "0xed64c6ab39cdac7732c9c8457d48a0c17a1f76a8");
    }

    @Deprecated
    protected PrivGeoMarktCommitmentStorage(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    protected PrivGeoMarktCommitmentStorage(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, credentials, contractGasProvider);
    }

    @Deprecated
    protected PrivGeoMarktCommitmentStorage(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    protected PrivGeoMarktCommitmentStorage(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public RemoteCall<Tuple5<byte[], byte[], byte[], byte[], BigInteger>> commitmentPublicParameters(String param0) {
        final Function function = new Function(FUNC_COMMITMENTPUBLICPARAMETERS, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(param0)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<DynamicBytes>() {}, new TypeReference<DynamicBytes>() {}, new TypeReference<DynamicBytes>() {}, new TypeReference<DynamicBytes>() {}, new TypeReference<Uint256>() {}));
        return new RemoteCall<Tuple5<byte[], byte[], byte[], byte[], BigInteger>>(
                new Callable<Tuple5<byte[], byte[], byte[], byte[], BigInteger>>() {
                    @Override
                    public Tuple5<byte[], byte[], byte[], byte[], BigInteger> call() throws Exception {
                        List<Type> results = executeCallMultipleValueReturn(function);
                        return new Tuple5<byte[], byte[], byte[], byte[], BigInteger>(
                                (byte[]) results.get(0).getValue(), 
                                (byte[]) results.get(1).getValue(), 
                                (byte[]) results.get(2).getValue(), 
                                (byte[]) results.get(3).getValue(), 
                                (BigInteger) results.get(4).getValue());
                    }
                });
    }

    public RemoteCall<String> ownerIndices(BigInteger param0) {
        final Function function = new Function(FUNC_OWNERINDICES, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(param0)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteCall<BigInteger> ownerIndicesMapping(String param0) {
        final Function function = new Function(FUNC_OWNERINDICESMAPPING, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(param0)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<Tuple3<byte[], BigInteger, BigInteger>> commitments(String param0, byte[] param1) {
        final Function function = new Function(FUNC_COMMITMENTS, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(param0), 
                new org.web3j.abi.datatypes.generated.Bytes32(param1)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<DynamicBytes>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}));
        return new RemoteCall<Tuple3<byte[], BigInteger, BigInteger>>(
                new Callable<Tuple3<byte[], BigInteger, BigInteger>>() {
                    @Override
                    public Tuple3<byte[], BigInteger, BigInteger> call() throws Exception {
                        List<Type> results = executeCallMultipleValueReturn(function);
                        return new Tuple3<byte[], BigInteger, BigInteger>(
                                (byte[]) results.get(0).getValue(), 
                                (BigInteger) results.get(1).getValue(), 
                                (BigInteger) results.get(2).getValue());
                    }
                });
    }

    public RemoteCall<byte[]> commitmentsIndicesMapping(String param0, BigInteger param1) {
        final Function function = new Function(FUNC_COMMITMENTSINDICESMAPPING, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(param0), 
                new org.web3j.abi.datatypes.generated.Uint256(param1)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bytes32>() {}));
        return executeRemoteCallSingleValueReturn(function, byte[].class);
    }

    public static RemoteCall<PrivGeoMarktCommitmentStorage> deploy(Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        return deployRemoteCall(PrivGeoMarktCommitmentStorage.class, web3j, credentials, contractGasProvider, BINARY, "");
    }

    public static RemoteCall<PrivGeoMarktCommitmentStorage> deploy(Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return deployRemoteCall(PrivGeoMarktCommitmentStorage.class, web3j, transactionManager, contractGasProvider, BINARY, "");
    }

    @Deprecated
    public static RemoteCall<PrivGeoMarktCommitmentStorage> deploy(Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(PrivGeoMarktCommitmentStorage.class, web3j, credentials, gasPrice, gasLimit, BINARY, "");
    }

    @Deprecated
    public static RemoteCall<PrivGeoMarktCommitmentStorage> deploy(Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(PrivGeoMarktCommitmentStorage.class, web3j, transactionManager, gasPrice, gasLimit, BINARY, "");
    }

    public RemoteCall<TransactionReceipt> createCommitmentPublicParameters(byte[] N_, byte[] a_, byte[] s_, byte[] c_) {
        final Function function = new Function(
                FUNC_CREATECOMMITMENTPUBLICPARAMETERS, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.DynamicBytes(N_), 
                new org.web3j.abi.datatypes.DynamicBytes(a_), 
                new org.web3j.abi.datatypes.DynamicBytes(s_), 
                new org.web3j.abi.datatypes.DynamicBytes(c_)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<Tuple5<byte[], byte[], byte[], byte[], BigInteger>> getCommitmentPublicParameters(String owner) {
        final Function function = new Function(FUNC_GETCOMMITMENTPUBLICPARAMETERS, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(owner)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<DynamicBytes>() {}, new TypeReference<DynamicBytes>() {}, new TypeReference<DynamicBytes>() {}, new TypeReference<DynamicBytes>() {}, new TypeReference<Uint256>() {}));
        return new RemoteCall<Tuple5<byte[], byte[], byte[], byte[], BigInteger>>(
                new Callable<Tuple5<byte[], byte[], byte[], byte[], BigInteger>>() {
                    @Override
                    public Tuple5<byte[], byte[], byte[], byte[], BigInteger> call() throws Exception {
                        List<Type> results = executeCallMultipleValueReturn(function);
                        return new Tuple5<byte[], byte[], byte[], byte[], BigInteger>(
                                (byte[]) results.get(0).getValue(), 
                                (byte[]) results.get(1).getValue(), 
                                (byte[]) results.get(2).getValue(), 
                                (byte[]) results.get(3).getValue(), 
                                (BigInteger) results.get(4).getValue());
                    }
                });
    }

    public RemoteCall<TransactionReceipt> deleteCommitmentPublicParameter() {
        final Function function = new Function(
                FUNC_DELETECOMMITMENTPUBLICPARAMETER, 
                Arrays.<Type>asList(), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<BigInteger> getOwnerCount() {
        final Function function = new Function(FUNC_GETOWNERCOUNT, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<BigInteger> getNumCommitments(String owner) {
        final Function function = new Function(FUNC_GETNUMCOMMITMENTS, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(owner)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<Tuple2<byte[], BigInteger>> getCommitment(String owner, byte[] dataAddress) {
        final Function function = new Function(FUNC_GETCOMMITMENT, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(owner), 
                new org.web3j.abi.datatypes.generated.Bytes32(dataAddress)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<DynamicBytes>() {}, new TypeReference<Uint256>() {}));
        return new RemoteCall<Tuple2<byte[], BigInteger>>(
                new Callable<Tuple2<byte[], BigInteger>>() {
                    @Override
                    public Tuple2<byte[], BigInteger> call() throws Exception {
                        List<Type> results = executeCallMultipleValueReturn(function);
                        return new Tuple2<byte[], BigInteger>(
                                (byte[]) results.get(0).getValue(), 
                                (BigInteger) results.get(1).getValue());
                    }
                });
    }

    public RemoteCall<Boolean> didOwnerHaveCommitments(String owner) {
        final Function function = new Function(FUNC_DIDOWNERHAVECOMMITMENTS, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(owner)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return executeRemoteCallSingleValueReturn(function, Boolean.class);
    }

    public RemoteCall<Boolean> didCommitmentHaveData(String owner, byte[] dataAddress) {
        final Function function = new Function(FUNC_DIDCOMMITMENTHAVEDATA, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(owner), 
                new org.web3j.abi.datatypes.generated.Bytes32(dataAddress)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return executeRemoteCallSingleValueReturn(function, Boolean.class);
    }

    public RemoteCall<TransactionReceipt> submitCommitment(byte[] dataAddress, byte[] commitment_) {
        final Function function = new Function(
                FUNC_SUBMITCOMMITMENT, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(dataAddress), 
                new org.web3j.abi.datatypes.DynamicBytes(commitment_)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> deleteCommitment(byte[] dataAddress) {
        final Function function = new Function(
                FUNC_DELETECOMMITMENT, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(dataAddress)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> deleteOwner() {
        final Function function = new Function(
                FUNC_DELETEOWNER, 
                Arrays.<Type>asList(), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    @Deprecated
    public static PrivGeoMarktCommitmentStorage load(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new PrivGeoMarktCommitmentStorage(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    @Deprecated
    public static PrivGeoMarktCommitmentStorage load(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new PrivGeoMarktCommitmentStorage(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public static PrivGeoMarktCommitmentStorage load(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        return new PrivGeoMarktCommitmentStorage(contractAddress, web3j, credentials, contractGasProvider);
    }

    public static PrivGeoMarktCommitmentStorage load(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return new PrivGeoMarktCommitmentStorage(contractAddress, web3j, transactionManager, contractGasProvider);
    }

    protected String getStaticDeployedAddress(String networkId) {
        return _addresses.get(networkId);
    }

    public static String getPreviouslyDeployedAddress(String networkId) {
        return _addresses.get(networkId);
    }
}
