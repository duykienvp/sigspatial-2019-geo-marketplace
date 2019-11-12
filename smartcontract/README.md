# Ethereum Smart Contract
The [Ethereum][eth] smart contract for this research project. The smart contract is written in [Solidity][solidity], 
using [Truffle][truffle] framework.

## Requirements
- [NodeJS][nodejs]: tested on v8.10.0
- [Truffle][truffle]: tested on v4.1.14


## Usage
Please refer to the [Truffle][truffle] documentation for more details about using [Truffle][truffle] to develop [Ethereum][eth] smart contracts. The following instructions only show general steps:
- Code the smart contracts in [Solidity][solidity] and place them in the [contracts](/smartcontract/contracts) folder.
- Update deployment script in [migrations](/smartcontract/migrations) folder.
- Compile: `truffle compile`
- Migrate: `truffle migrate`
- Migrate with reset: `truffle migrate --reset`

Then one can use scripts [generate_java_classes.sh](/scripts/generate_java_classes.sh) to generate java files

If you need to migrate them to a [Ethereum][eth] network, please refer to the [Truffle][truffle] documentation.

[eth]:https://www.ethereum.org/
[solidity]:https://github.com/ethereum/solidity
[nodejs]:https://nodejs.org/
[truffle]: https://truffleframework.com/truffle
