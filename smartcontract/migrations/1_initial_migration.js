const Web3 = require('web3');

const TruffleConfig = require('../truffle');

var Migrations = artifacts.require("./Migrations.sol");


module.exports = function(deployer, network, addresses) {
  console.log('>> Deploying migration');
  deployer.deploy(Migrations);
};
