package edu.usc.infolab.kien.blockchaingeospatial.contract.generated;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Callable;
import org.web3j.abi.EventEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Bool;
import org.web3j.abi.datatypes.DynamicArray;
import org.web3j.abi.datatypes.DynamicBytes;
import org.web3j.abi.datatypes.Event;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.generated.Bytes32;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.abi.datatypes.generated.Uint8;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.response.Log;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tuples.generated.Tuple10;
import org.web3j.tuples.generated.Tuple2;
import org.web3j.tuples.generated.Tuple6;
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
public class PrivGeoMarkt extends Contract {
    private static final String BINARY = "0x608060405234801561001057600080fd5b50612a34806100206000396000f300608060405260043610610154576000357c0100000000000000000000000000000000000000000000000000000000900463ffffffff1680630783ba54146101595780630800b50f146101ba5780630c007cf514610267578063167204b21461030f57806319f8d0da1461033c578063219f65b8146103f85780632ca00a1f146104b45780633e29aab21461051557806360ae8ee7146105ad5780637073ec14146105f25780637110f1801461064957806375e2529b146106a05780637b84e5ee146106e55780637ed115b51461077d57806385b8f318146107945780638a72ea6a146107eb578063920e247f146109365780639cf1ffdd14610961578063a91d58b4146109a9578063c55ce58114610a12578063d2375b8f14610a69578063dbd3cd6214610af6578063dc2d98d214610b3b578063e0db0b8714610b8a578063e3e9dd9714610c87578063ef706adf14610d1c575b600080fd5b34801561016557600080fd5b506101a4600480360381019080803573ffffffffffffffffffffffffffffffffffffffff16906020019092919080359060200190929190505050610d61565b6040518082815260200191505060405180910390f35b3480156101c657600080fd5b506101e560048036038101908080359060200190929190505050610d91565b6040518080602001838152602001828103825284818151815260200191508051906020019080838360005b8381101561022b578082015181840152602081019050610210565b50505050905090810190601f1680156102585780820380516001836020036101000a031916815260200191505b50935050505060405180910390f35b34801561027357600080fd5b506102f5600480360381019080803573ffffffffffffffffffffffffffffffffffffffff1690602001909291908035906020019082018035906020019080806020026020016040519081016040528093929190818152602001838360200280828437820191505050505050919291929080359060200190929190505050610f1d565b604051808215151515815260200191505060405180910390f35b34801561031b57600080fd5b5061033a60048036038101908080359060200190929190505050610f70565b005b34801561034857600080fd5b5061037d600480360381019080803573ffffffffffffffffffffffffffffffffffffffff169060200190929190505050610fb9565b6040518080602001828103825283818151815260200191508051906020019080838360005b838110156103bd5780820151818401526020810190506103a2565b50505050905090810190601f1680156103ea5780820380516001836020036101000a031916815260200191505b509250505060405180910390f35b34801561040457600080fd5b50610439600480360381019080803573ffffffffffffffffffffffffffffffffffffffff16906020019092919050505061109a565b6040518080602001828103825283818151815260200191508051906020019080838360005b8381101561047957808201518184015260208101905061045e565b50505050905090810190601f1680156104a65780820380516001836020036101000a031916815260200191505b509250505060405180910390f35b3480156104c057600080fd5b506104ff600480360381019080803573ffffffffffffffffffffffffffffffffffffffff1690602001909291908035906020019092919050505061114a565b6040518082815260200191505060405180910390f35b34801561052157600080fd5b50610556600480360381019080803573ffffffffffffffffffffffffffffffffffffffff16906020019092919050505061117a565b6040518080602001828103825283818151815260200191508051906020019060200280838360005b8381101561059957808201518184015260208101905061057e565b505050509050019250505060405180910390f35b3480156105b957600080fd5b506105d860048036038101908080359060200190929190505050611211565b604051808215151515815260200191505060405180910390f35b3480156105fe57600080fd5b50610633600480360381019080803573ffffffffffffffffffffffffffffffffffffffff169060200190929190505050611482565b6040518082815260200191505060405180910390f35b34801561065557600080fd5b5061068a600480360381019080803573ffffffffffffffffffffffffffffffffffffffff1690602001909291905050506114ce565b6040518082815260200191505060405180910390f35b3480156106ac57600080fd5b506106cb600480360381019080803590602001909291905050506114ec565b604051808215151515815260200191505060405180910390f35b3480156106f157600080fd5b50610726600480360381019080803573ffffffffffffffffffffffffffffffffffffffff1690602001909291905050506116bb565b6040518080602001828103825283818151815260200191508051906020019060200280838360005b8381101561076957808201518184015260208101905061074e565b505050509050019250505060405180910390f35b34801561078957600080fd5b50610792611752565b005b3480156107a057600080fd5b506107d5600480360381019080803573ffffffffffffffffffffffffffffffffffffffff16906020019092919050505061179d565b6040518082815260200191505060405180910390f35b3480156107f757600080fd5b50610816600480360381019080803590602001909291905050506117e8565b604051808b73ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020018a73ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001898152602001888152602001878152602001806020018681526020018581526020018481526020018360058111156108b257fe5b60ff168152602001828103825287818151815260200191508051906020019080838360005b838110156108f25780820151818401526020810190506108d7565b50505050905090810190601f16801561091f5780820380516001836020036101000a031916815260200191505b509b50505050505050505050505060405180910390f35b34801561094257600080fd5b5061094b611930565b6040518082815260200191505060405180910390f35b34801561096d57600080fd5b5061098c6004803603810190808035906020019092919050505061193d565b604051808381526020018281526020019250505060405180910390f35b3480156109b557600080fd5b50610a10600480360381019080803590602001908201803590602001908080601f0160208091040260200160405190810160405280939291908181526020018383808284378201915050505050509192919290505050611a2d565b005b348015610a1e57600080fd5b50610a53600480360381019080803573ffffffffffffffffffffffffffffffffffffffff169060200190929190505050611a84565b6040518082815260200191505060405180910390f35b610af4600480360381019080803573ffffffffffffffffffffffffffffffffffffffff169060200190929190803590602001908201803590602001908080602002602001604051908101604052809392919081815260200183836020028082843782019150505050505091929192908035906020019092919080359060200190929190505050611ad0565b005b348015610b0257600080fd5b50610b2160048036038101908080359060200190929190505050611f8b565b604051808215151515815260200191505060405180910390f35b348015610b4757600080fd5b50610b66600480360381019080803590602001909291905050506120f8565b60405180826005811115610b7657fe5b60ff16815260200191505060405180910390f35b348015610b9657600080fd5b50610bb5600480360381019080803590602001909291905050506121d1565b604051808773ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020018673ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200185815260200184815260200183815260200180602001828103825283818151815260200191508051906020019060200280838360005b83811015610c6e578082015181840152602081019050610c53565b5050505090500197505050505050505060405180910390f35b348015610c9357600080fd5b50610d0260048036038101908080359060200190929190803590602001908201803590602001908080601f0160208091040260200160405190810160405280939291908181526020018383808284378201915050505050509192919290803590602001909291905050506123ed565b604051808215151515815260200191505060405180910390f35b348015610d2857600080fd5b50610d476004803603810190808035906020019092919050505061264f565b604051808215151515815260200191505060405180910390f35b600360205281600052604060002081815481101515610d7c57fe5b90600052602060002001600091509150505481565b6060600060028054905083101515610e37576040517f08c379a000000000000000000000000000000000000000000000000000000000815260040180806020018281038252603b8152602001807f496e76616c696420696e646578206f6620746865206f6666657220696e20746881526020017f65206f66666572206c69737420746f2062652073656c6563746564000000000081525060400191505060405180910390fd5b600283815481101515610e4657fe5b90600052602060002090600b0201600601600284815481101515610e6657fe5b90600052602060002090600b020160070154818054600181600116156101000203166002900480601f016020809104026020016040519081016040528092919081815260200182805460018160011615610100020316600290048015610f0d5780601f10610ee257610100808354040283529160200191610f0d565b820191906000526020600020905b815481529060010190602001808311610ef057829003601f168201915b5050505050915091509150915091565b60008183516000808773ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020019081526020016000206000015402111590509392505050565b806000803373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020019081526020016000206000018190555050565b6060600160008373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020019081526020016000208054600181600116156101000203166002900480601f01602080910402602001604051908101604052809291908181526020018280546001816001161561010002031660029004801561108e5780601f106110635761010080835404028352916020019161108e565b820191906000526020600020905b81548152906001019060200180831161107157829003601f168201915b50505050509050919050565b60016020528060005260406000206000915090508054600181600116156101000203166002900480601f0160208091040260200160405190810160405280929190818152602001828054600181600116156101000203166002900480156111425780601f1061111757610100808354040283529160200191611142565b820191906000526020600020905b81548152906001019060200180831161112557829003601f168201915b505050505081565b60046020528160005260406000208181548110151561116557fe5b90600052602060002001600091509150505481565b6060600360008373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002080548060200260200160405190810160405280929190818152602001828054801561120557602002820191906000526020600020905b8154815260200190600101908083116111f1575b50505050509050919050565b6000806000806000600280549050861015156112305760009450611479565b60028681548110151561123f57fe5b90600052602060002090600b020160000160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff16935060028681548110151561128257fe5b90600052602060002090600b020160010160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1692506002868154811015156112c557fe5b90600052602060002090600b0201600901549150600460008573ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001908152602001600020805490508210151561132d5760009450611479565b600460008573ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020019081526020016000208281548110151561137957fe5b9060005260206000200154861415156113955760009450611479565b6002868154811015156113a457fe5b90600052602060002090600b0201600801549050600360008473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001908152602001600020805490508110151561140c5760009450611479565b600360008473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020019081526020016000208181548110151561145857fe5b9060005260206000200154861415156114745760009450611479565b600194505b50505050919050565b6000600360008373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001908152602001600020805490509050919050565b60006020528060005260406000206000915090508060000154905081565b6000806000806114fb85611211565b151561150a57600093506116b3565b6000925060028581548110151561151d57fe5b90600052602060002090600b020191506001600581111561153a57fe5b82600a0160009054906101000a900460ff16600581111561155757fe5b148061158a57506002600581111561156b57fe5b82600a0160009054906101000a900460ff16600581111561158857fe5b145b90503373ffffffffffffffffffffffffffffffffffffffff168260000160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff161480156115e85750805b80156115f75750428260030154105b80156116065750816004015442105b156116ae5760028581548110151561161a57fe5b90600052602060002090600b0201600201549250600582600a0160006101000a81548160ff0219169083600581111561164f57fe5b021790555082600010156116a5573373ffffffffffffffffffffffffffffffffffffffff166108fc849081150290604051600060405180830381858888f193505050501580156116a3573d6000803e3d6000fd5b505b600193506116b3565b600093505b505050919050565b6060600360008373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002080548060200260200160405190810160405280929190818152602001828054801561174657602002820191906000526020600020905b815481526020019060010190808311611732575b50505050509050919050565b6000803373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020019081526020016000206000808201600090555050565b60008060008373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001908152602001600020600001549050919050565b6002818154811015156117f757fe5b90600052602060002090600b02016000915090508060000160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff16908060010160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1690806002015490806003015490806004015490806006018054600181600116156101000203166002900480601f0160208091040260200160405190810160405280929190818152602001828054600181600116156101000203166002900480156119015780601f106118d657610100808354040283529160200191611901565b820191906000526020600020905b8154815290600101906020018083116118e457829003601f168201915b50505050509080600701549080600801549080600901549080600a0160009054906101000a900460ff1690508a565b6000600280549050905090565b600080600280549050831015156119e2576040517f08c379a000000000000000000000000000000000000000000000000000000000815260040180806020018281038252603b8152602001807f496e76616c696420696e646578206f6620746865206f6666657220696e20746881526020017f65206f66666572206c69737420746f2062652073656c6563746564000000000081525060400191505060405180910390fd5b6002838154811015156119f157fe5b90600052602060002090600b020160030154600284815481101515611a1257fe5b90600052602060002090600b02016004015491509150915091565b80600160003373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020019081526020016000209080519060200190611a809291906127d9565b5050565b6000600460008373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001908152602001600020805490509050919050565b6000611ada612859565b600085516000101515611b55576040517f08c379a00000000000000000000000000000000000000000000000000000000081526004018080602001828103825260158152602001807f456d7074792064617461206974656d73206c697374000000000000000000000081525060200191505060405180910390fd5b8385101515611bf2576040517f08c379a00000000000000000000000000000000000000000000000000000000081526004018080602001828103825260398152602001807f4f666665722065786970726174696f6e2073686f756c64206265206561726c6981526020017f6572207468616e20526566756e642065787069726174696f6e0000000000000081525060400191505060405180910390fd5b339250611c00878734610f1d565b1515611c74576040517f08c379a00000000000000000000000000000000000000000000000000000000081526004018080602001828103825260208152602001807f507572636861736520706f6c696379206973206e6f742073617469736669656481525060200191505060405180910390fd5b82826000019073ffffffffffffffffffffffffffffffffffffffff16908173ffffffffffffffffffffffffffffffffffffffff168152505086826020019073ffffffffffffffffffffffffffffffffffffffff16908173ffffffffffffffffffffffffffffffffffffffff1681525050348260400181815250508482606001818152505083826080018181525050858260a0018190525060028054905090506001600360008973ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001908152602001600020829080600181540180825580915050906001820390600052602060002001600090919290919091505503826101000181815250506001600460008573ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020019081526020016000208290806001815401808255809150509060018203906000526020600020016000909192909190915055038261012001818152505060018261014001906005811115611e1057fe5b90816005811115611e1d57fe5b815250506002829080600181540180825580915050906001820390600052602060002090600b02016000909192909190915060008201518160000160006101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff16021790555060208201518160010160006101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff16021790555060408201518160020155606082015181600301556080820151816004015560a0820151816005019080519060200190611f179291906128eb565b5060c0820151816006019080519060200190611f3492919061293e565b5060e082015181600701556101008201518160080155610120820151816009015561014082015181600a0160006101000a81548160ff02191690836005811115611f7a57fe5b021790555050505050505050505050565b6000806000611f9984611211565b1515611fa857600092506120f1565b60009150600284815481101515611fbb57fe5b90600052602060002090600b020190503373ffffffffffffffffffffffffffffffffffffffff168160010160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1614801561205157506002600581111561203257fe5b81600a0160009054906101000a900460ff16600581111561204f57fe5b145b80156120605750428160040154105b156120ec5780600201549150600481600a0160006101000a81548160ff0219169083600581111561208d57fe5b021790555081600010156120e3573373ffffffffffffffffffffffffffffffffffffffff166108fc839081150290604051600060405180830381858888f193505050501580156120e1573d6000803e3d6000fd5b505b600192506120f1565b600092505b5050919050565b60006002805490508210151561219c576040517f08c379a000000000000000000000000000000000000000000000000000000000815260040180806020018281038252603b8152602001807f496e76616c696420696e646578206f6620746865206f6666657220696e20746881526020017f65206f66666572206c69737420746f2062652073656c6563746564000000000081525060400191505060405180910390fd5b6002828154811015156121ab57fe5b90600052602060002090600b0201600a0160009054906101000a900460ff169050919050565b600080600080600060606002805490508710151561227d576040517f08c379a000000000000000000000000000000000000000000000000000000000815260040180806020018281038252603b8152602001807f496e76616c696420696e646578206f6620746865206f6666657220696e20746881526020017f65206f66666572206c69737420746f2062652073656c6563746564000000000081525060400191505060405180910390fd5b60028781548110151561228c57fe5b90600052602060002090600b020160000160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff166002888154811015156122cd57fe5b90600052602060002090600b020160010160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1660028981548110151561230e57fe5b90600052602060002090600b02016002015460028a81548110151561232f57fe5b90600052602060002090600b02016008015460028b81548110151561235057fe5b90600052602060002090600b02016009015460028c81548110151561237157fe5b90600052602060002090600b0201600501808054806020026020016040519081016040528092919081815260200182805480156123d157602002820191906000526020600020905b815460001916815260200190600101908083116123b9575b5050505050905095509550955095509550955091939550919395565b6000806123f985611211565b15156124085760009150612647565b60028581548110151561241757fe5b90600052602060002090600b020190503373ffffffffffffffffffffffffffffffffffffffff168160010160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff161480156124ad57506001600581111561248e57fe5b81600a0160009054906101000a900460ff1660058111156124ab57fe5b145b80156124bc5750806003015442105b1561264257838160060190805190602001906124d99291906127d9565b50828160070181905550600281600a0160006101000a81548160ff0219169083600581111561250457fe5b02179055507f8d38052e30bacccfae84fb046949633382fc874b5e16f88e6e8f84ddc71a1e86338260000160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff168686604051808573ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020018473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200180602001838152602001828103825284818151815260200191508051906020019080838360005b838110156125fc5780820151818401526020810190506125e1565b50505050905090810190601f1680156126295780820380516001836020036101000a031916815260200191505b509550505050505060405180910390a160019150612647565b600091505b509392505050565b600080600061265d84611211565b151561266c57600092506127d2565b6000915060028481548110151561267f57fe5b90600052602060002090600b020190503373ffffffffffffffffffffffffffffffffffffffff168160000160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff161480156127155750600160058111156126f657fe5b81600a0160009054906101000a900460ff16600581111561271357fe5b145b8015612725575060008160070154145b156127cd5760028481548110151561273957fe5b90600052602060002090600b0201600201549150600381600a0160006101000a81548160ff0219169083600581111561276e57fe5b021790555081600010156127c4573373ffffffffffffffffffffffffffffffffffffffff166108fc839081150290604051600060405180830381858888f193505050501580156127c2573d6000803e3d6000fd5b505b600192506127d2565b600092505b5050919050565b828054600181600116156101000203166002900490600052602060002090601f016020900481019282601f1061281a57805160ff1916838001178555612848565b82800160010185558215612848579182015b8281111561284757825182559160200191906001019061282c565b5b50905061285591906129be565b5090565b61016060405190810160405280600073ffffffffffffffffffffffffffffffffffffffff168152602001600073ffffffffffffffffffffffffffffffffffffffff1681526020016000815260200160008152602001600081526020016060815260200160608152602001600081526020016000815260200160008152602001600060058111156128e557fe5b81525090565b82805482825590600052602060002090810192821561292d579160200282015b8281111561292c57825182906000191690559160200191906001019061290b565b5b50905061293a91906129e3565b5090565b828054600181600116156101000203166002900490600052602060002090601f016020900481019282601f1061297f57805160ff19168380011785556129ad565b828001600101855582156129ad579182015b828111156129ac578251825591602001919060010190612991565b5b5090506129ba91906129be565b5090565b6129e091905b808211156129dc5760008160009055506001016129c4565b5090565b90565b612a0591905b80821115612a015760008160009055506001016129e9565b5090565b905600a165627a7a723058209866f9ea76103801b2c561986ac786baf83143c7faeba0723fbeff6a8ef097450029";

    public static final String FUNC_OFFERSOFOWNER = "offersOfOwner";

    public static final String FUNC_BUYERPUBLICKEY = "buyerPublicKey";

    public static final String FUNC_OFFERSOFBUYER = "offersOfBuyer";

    public static final String FUNC_PURCHASEPOLICIES = "purchasePolicies";

    public static final String FUNC_OFFERS = "offers";

    public static final String FUNC_GETPURCHASEPOLICYOFOWNER = "getPurchasePolicyOfOwner";

    public static final String FUNC_CREATEPURCHASEPOLICY = "createPurchasePolicy";

    public static final String FUNC_DELETEPURCHASEPOLICY = "deletePurchasePolicy";

    public static final String FUNC_ISSATISFIABLE = "isSatisfiable";

    public static final String FUNC_GETPUBLICKEYOFBUYER = "getPublicKeyOfBuyer";

    public static final String FUNC_SETPUBLICKEY = "setPublicKey";

    public static final String FUNC_GETNUMOFFERSOFBUYER = "getNumOffersOfBuyer";

    public static final String FUNC_GETNUMOFFERSOFOWNERS = "getNumOffersOfOwners";

    public static final String FUNC_GETOFFERINDICESOFBUYERS = "getOfferIndicesOfBuyers";

    public static final String FUNC_GETOFFERINDICESOFOWNERS = "getOfferIndicesOfOwners";

    public static final String FUNC_GETNUMOFFERS = "getNumOffers";

    public static final String FUNC_GETOFFERCONTRIBUTORS = "getOfferContributors";

    public static final String FUNC_GETOFFEREXPIRATIONTIMES = "getOfferExpirationTimes";

    public static final String FUNC_GETOFFERKEYS = "getOfferKeys";

    public static final String FUNC_GETOFFERSTATUS = "getOfferStatus";

    public static final String FUNC_MAKEOFFER = "makeOffer";

    public static final String FUNC_ISVALIDOFFERMAPPING = "isValidOfferMapping";

    public static final String FUNC_CANCELOFFER = "cancelOffer";

    public static final String FUNC_SENDKEYS = "sendKeys";

    public static final String FUNC_WITHDRAWPAYMENT = "withdrawPayment";

    public static final String FUNC_REFUNDOFFER = "refundOffer";

    public static final Event OFFERMADE_EVENT = new Event("offerMade", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}, new TypeReference<Address>() {}));
    ;

    public static final Event SENDKEYSEVENT_EVENT = new Event("sendKeysEvent", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}, new TypeReference<Address>() {}, new TypeReference<DynamicBytes>() {}, new TypeReference<Uint256>() {}));
    ;

    protected static final HashMap<String, String> _addresses;

    static {
        _addresses = new HashMap<String, String>();
        _addresses.put("1", "0x1c7c91996550252273a9a133d2539b700e066408");
    }

    @Deprecated
    protected PrivGeoMarkt(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    protected PrivGeoMarkt(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, credentials, contractGasProvider);
    }

    @Deprecated
    protected PrivGeoMarkt(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    protected PrivGeoMarkt(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public RemoteCall<BigInteger> offersOfOwner(String param0, BigInteger param1) {
        final Function function = new Function(FUNC_OFFERSOFOWNER, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(param0), 
                new org.web3j.abi.datatypes.generated.Uint256(param1)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<byte[]> buyerPublicKey(String param0) {
        final Function function = new Function(FUNC_BUYERPUBLICKEY, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(param0)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<DynamicBytes>() {}));
        return executeRemoteCallSingleValueReturn(function, byte[].class);
    }

    public RemoteCall<BigInteger> offersOfBuyer(String param0, BigInteger param1) {
        final Function function = new Function(FUNC_OFFERSOFBUYER, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(param0), 
                new org.web3j.abi.datatypes.generated.Uint256(param1)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<BigInteger> purchasePolicies(String param0) {
        final Function function = new Function(FUNC_PURCHASEPOLICIES, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(param0)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<Tuple10<String, String, BigInteger, BigInteger, BigInteger, byte[], BigInteger, BigInteger, BigInteger, BigInteger>> offers(BigInteger param0) {
        final Function function = new Function(FUNC_OFFERS, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(param0)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}, new TypeReference<Address>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}, new TypeReference<DynamicBytes>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint8>() {}));
        return new RemoteCall<Tuple10<String, String, BigInteger, BigInteger, BigInteger, byte[], BigInteger, BigInteger, BigInteger, BigInteger>>(
                new Callable<Tuple10<String, String, BigInteger, BigInteger, BigInteger, byte[], BigInteger, BigInteger, BigInteger, BigInteger>>() {
                    @Override
                    public Tuple10<String, String, BigInteger, BigInteger, BigInteger, byte[], BigInteger, BigInteger, BigInteger, BigInteger> call() throws Exception {
                        List<Type> results = executeCallMultipleValueReturn(function);
                        return new Tuple10<String, String, BigInteger, BigInteger, BigInteger, byte[], BigInteger, BigInteger, BigInteger, BigInteger>(
                                (String) results.get(0).getValue(), 
                                (String) results.get(1).getValue(), 
                                (BigInteger) results.get(2).getValue(), 
                                (BigInteger) results.get(3).getValue(), 
                                (BigInteger) results.get(4).getValue(), 
                                (byte[]) results.get(5).getValue(), 
                                (BigInteger) results.get(6).getValue(), 
                                (BigInteger) results.get(7).getValue(), 
                                (BigInteger) results.get(8).getValue(), 
                                (BigInteger) results.get(9).getValue());
                    }
                });
    }

    public static RemoteCall<PrivGeoMarkt> deploy(Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        return deployRemoteCall(PrivGeoMarkt.class, web3j, credentials, contractGasProvider, BINARY, "");
    }

    public static RemoteCall<PrivGeoMarkt> deploy(Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return deployRemoteCall(PrivGeoMarkt.class, web3j, transactionManager, contractGasProvider, BINARY, "");
    }

    @Deprecated
    public static RemoteCall<PrivGeoMarkt> deploy(Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(PrivGeoMarkt.class, web3j, credentials, gasPrice, gasLimit, BINARY, "");
    }

    @Deprecated
    public static RemoteCall<PrivGeoMarkt> deploy(Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(PrivGeoMarkt.class, web3j, transactionManager, gasPrice, gasLimit, BINARY, "");
    }

    public List<OfferMadeEventResponse> getOfferMadeEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(OFFERMADE_EVENT, transactionReceipt);
        ArrayList<OfferMadeEventResponse> responses = new ArrayList<OfferMadeEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            OfferMadeEventResponse typedResponse = new OfferMadeEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.owner = (String) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.buyer = (String) eventValues.getNonIndexedValues().get(1).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Observable<OfferMadeEventResponse> offerMadeEventObservable(EthFilter filter) {
        return web3j.ethLogObservable(filter).map(new Func1<Log, OfferMadeEventResponse>() {
            @Override
            public OfferMadeEventResponse call(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(OFFERMADE_EVENT, log);
                OfferMadeEventResponse typedResponse = new OfferMadeEventResponse();
                typedResponse.log = log;
                typedResponse.owner = (String) eventValues.getNonIndexedValues().get(0).getValue();
                typedResponse.buyer = (String) eventValues.getNonIndexedValues().get(1).getValue();
                return typedResponse;
            }
        });
    }

    public Observable<OfferMadeEventResponse> offerMadeEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(OFFERMADE_EVENT));
        return offerMadeEventObservable(filter);
    }

    public List<SendKeysEventEventResponse> getSendKeysEventEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(SENDKEYSEVENT_EVENT, transactionReceipt);
        ArrayList<SendKeysEventEventResponse> responses = new ArrayList<SendKeysEventEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            SendKeysEventEventResponse typedResponse = new SendKeysEventEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.owner = (String) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.buyer = (String) eventValues.getNonIndexedValues().get(1).getValue();
            typedResponse.encryptedKeys = (byte[]) eventValues.getNonIndexedValues().get(2).getValue();
            typedResponse.numEncryptedKeys = (BigInteger) eventValues.getNonIndexedValues().get(3).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Observable<SendKeysEventEventResponse> sendKeysEventEventObservable(EthFilter filter) {
        return web3j.ethLogObservable(filter).map(new Func1<Log, SendKeysEventEventResponse>() {
            @Override
            public SendKeysEventEventResponse call(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(SENDKEYSEVENT_EVENT, log);
                SendKeysEventEventResponse typedResponse = new SendKeysEventEventResponse();
                typedResponse.log = log;
                typedResponse.owner = (String) eventValues.getNonIndexedValues().get(0).getValue();
                typedResponse.buyer = (String) eventValues.getNonIndexedValues().get(1).getValue();
                typedResponse.encryptedKeys = (byte[]) eventValues.getNonIndexedValues().get(2).getValue();
                typedResponse.numEncryptedKeys = (BigInteger) eventValues.getNonIndexedValues().get(3).getValue();
                return typedResponse;
            }
        });
    }

    public Observable<SendKeysEventEventResponse> sendKeysEventEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(SENDKEYSEVENT_EVENT));
        return sendKeysEventEventObservable(filter);
    }

    public RemoteCall<BigInteger> getPurchasePolicyOfOwner(String owner) {
        final Function function = new Function(FUNC_GETPURCHASEPOLICYOFOWNER, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(owner)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<TransactionReceipt> createPurchasePolicy(BigInteger minPrice_) {
        final Function function = new Function(
                FUNC_CREATEPURCHASEPOLICY, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(minPrice_)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> deletePurchasePolicy() {
        final Function function = new Function(
                FUNC_DELETEPURCHASEPOLICY, 
                Arrays.<Type>asList(), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<Boolean> isSatisfiable(String owner, List<byte[]> dataAddresses, BigInteger value) {
        final Function function = new Function(FUNC_ISSATISFIABLE, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(owner), 
                new org.web3j.abi.datatypes.DynamicArray<org.web3j.abi.datatypes.generated.Bytes32>(
                        org.web3j.abi.Utils.typeMap(dataAddresses, org.web3j.abi.datatypes.generated.Bytes32.class)), 
                new org.web3j.abi.datatypes.generated.Uint256(value)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return executeRemoteCallSingleValueReturn(function, Boolean.class);
    }

    public RemoteCall<byte[]> getPublicKeyOfBuyer(String buyer) {
        final Function function = new Function(FUNC_GETPUBLICKEYOFBUYER, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(buyer)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<DynamicBytes>() {}));
        return executeRemoteCallSingleValueReturn(function, byte[].class);
    }

    public RemoteCall<TransactionReceipt> setPublicKey(byte[] pk) {
        final Function function = new Function(
                FUNC_SETPUBLICKEY, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.DynamicBytes(pk)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<BigInteger> getNumOffersOfBuyer(String buyer) {
        final Function function = new Function(FUNC_GETNUMOFFERSOFBUYER, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(buyer)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<BigInteger> getNumOffersOfOwners(String owner) {
        final Function function = new Function(FUNC_GETNUMOFFERSOFOWNERS, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(owner)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<List> getOfferIndicesOfBuyers(String buyer) {
        final Function function = new Function(FUNC_GETOFFERINDICESOFBUYERS, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(buyer)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<DynamicArray<Uint256>>() {}));
        return new RemoteCall<List>(
                new Callable<List>() {
                    @Override
                    @SuppressWarnings("unchecked")
                    public List call() throws Exception {
                        List<Type> result = (List<Type>) executeCallSingleValueReturn(function, List.class);
                        return convertToNative(result);
                    }
                });
    }

    public RemoteCall<List> getOfferIndicesOfOwners(String owner) {
        final Function function = new Function(FUNC_GETOFFERINDICESOFOWNERS, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(owner)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<DynamicArray<Uint256>>() {}));
        return new RemoteCall<List>(
                new Callable<List>() {
                    @Override
                    @SuppressWarnings("unchecked")
                    public List call() throws Exception {
                        List<Type> result = (List<Type>) executeCallSingleValueReturn(function, List.class);
                        return convertToNative(result);
                    }
                });
    }

    public RemoteCall<BigInteger> getNumOffers() {
        final Function function = new Function(FUNC_GETNUMOFFERS, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<Tuple6<String, String, BigInteger, BigInteger, BigInteger, List<byte[]>>> getOfferContributors(BigInteger indexInOffersList) {
        final Function function = new Function(FUNC_GETOFFERCONTRIBUTORS, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(indexInOffersList)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}, new TypeReference<Address>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}, new TypeReference<DynamicArray<Bytes32>>() {}));
        return new RemoteCall<Tuple6<String, String, BigInteger, BigInteger, BigInteger, List<byte[]>>>(
                new Callable<Tuple6<String, String, BigInteger, BigInteger, BigInteger, List<byte[]>>>() {
                    @Override
                    public Tuple6<String, String, BigInteger, BigInteger, BigInteger, List<byte[]>> call() throws Exception {
                        List<Type> results = executeCallMultipleValueReturn(function);
                        return new Tuple6<String, String, BigInteger, BigInteger, BigInteger, List<byte[]>>(
                                (String) results.get(0).getValue(), 
                                (String) results.get(1).getValue(), 
                                (BigInteger) results.get(2).getValue(), 
                                (BigInteger) results.get(3).getValue(), 
                                (BigInteger) results.get(4).getValue(), 
                                convertToNative((List<Bytes32>) results.get(5).getValue()));
                    }
                });
    }

    public RemoteCall<Tuple2<BigInteger, BigInteger>> getOfferExpirationTimes(BigInteger indexInOffersList) {
        final Function function = new Function(FUNC_GETOFFEREXPIRATIONTIMES, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(indexInOffersList)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}));
        return new RemoteCall<Tuple2<BigInteger, BigInteger>>(
                new Callable<Tuple2<BigInteger, BigInteger>>() {
                    @Override
                    public Tuple2<BigInteger, BigInteger> call() throws Exception {
                        List<Type> results = executeCallMultipleValueReturn(function);
                        return new Tuple2<BigInteger, BigInteger>(
                                (BigInteger) results.get(0).getValue(), 
                                (BigInteger) results.get(1).getValue());
                    }
                });
    }

    public RemoteCall<Tuple2<byte[], BigInteger>> getOfferKeys(BigInteger indexInOffersList) {
        final Function function = new Function(FUNC_GETOFFERKEYS, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(indexInOffersList)), 
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

    public RemoteCall<BigInteger> getOfferStatus(BigInteger indexInOffersList) {
        final Function function = new Function(FUNC_GETOFFERSTATUS, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(indexInOffersList)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint8>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<TransactionReceipt> makeOffer(String owner, List<byte[]> dataAddresses_, BigInteger offerExpiration_, BigInteger refundExpiration_, BigInteger weiValue) {
        final Function function = new Function(
                FUNC_MAKEOFFER, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(owner), 
                new org.web3j.abi.datatypes.DynamicArray<org.web3j.abi.datatypes.generated.Bytes32>(
                        org.web3j.abi.Utils.typeMap(dataAddresses_, org.web3j.abi.datatypes.generated.Bytes32.class)), 
                new org.web3j.abi.datatypes.generated.Uint256(offerExpiration_), 
                new org.web3j.abi.datatypes.generated.Uint256(refundExpiration_)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function, weiValue);
    }

    public RemoteCall<Boolean> isValidOfferMapping(BigInteger indexInOffersList) {
        final Function function = new Function(FUNC_ISVALIDOFFERMAPPING, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(indexInOffersList)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return executeRemoteCallSingleValueReturn(function, Boolean.class);
    }

    public RemoteCall<TransactionReceipt> cancelOffer(BigInteger indexInOffersList) {
        final Function function = new Function(
                FUNC_CANCELOFFER, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(indexInOffersList)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> sendKeys(BigInteger indexInOffersList, byte[] encryptedKeys_, BigInteger numEncryptedKeys_) {
        final Function function = new Function(
                FUNC_SENDKEYS, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(indexInOffersList), 
                new org.web3j.abi.datatypes.DynamicBytes(encryptedKeys_), 
                new org.web3j.abi.datatypes.generated.Uint256(numEncryptedKeys_)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> withdrawPayment(BigInteger indexInOffersList) {
        final Function function = new Function(
                FUNC_WITHDRAWPAYMENT, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(indexInOffersList)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> refundOffer(BigInteger indexInOffersList) {
        final Function function = new Function(
                FUNC_REFUNDOFFER, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(indexInOffersList)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    @Deprecated
    public static PrivGeoMarkt load(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new PrivGeoMarkt(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    @Deprecated
    public static PrivGeoMarkt load(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new PrivGeoMarkt(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public static PrivGeoMarkt load(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        return new PrivGeoMarkt(contractAddress, web3j, credentials, contractGasProvider);
    }

    public static PrivGeoMarkt load(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return new PrivGeoMarkt(contractAddress, web3j, transactionManager, contractGasProvider);
    }

    protected String getStaticDeployedAddress(String networkId) {
        return _addresses.get(networkId);
    }

    public static String getPreviouslyDeployedAddress(String networkId) {
        return _addresses.get(networkId);
    }

    public static class OfferMadeEventResponse {
        public Log log;

        public String owner;

        public String buyer;
    }

    public static class SendKeysEventEventResponse {
        public Log log;

        public String owner;

        public String buyer;

        public byte[] encryptedKeys;

        public BigInteger numEncryptedKeys;
    }
}
