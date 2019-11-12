var PrivGeoMarktStorage = artifacts.require("./PrivGeoMarktStorage.sol");
var PrivGeoMarkt = artifacts.require("./PrivGeoMarkt.sol");
module.exports = function(callback) {
    var privGeoMarktStorage;
    var privGeoMarkt;
    PrivGeoMarktStorage.deployed()
    .then(function(instance) {
        privGeoMarktStorage = instance;
        return privGeoMarktStorage.getPurchasePoliciesCount.call();
    }).then(function(count) {
        // If this callback is called, the call was successfully executed.
        // Note that this returns immediately without any waiting.
        // Let's print the return value.
        console.log("Current number of purchase policy = ", count.toNumber());
        var minPrice = 2.3;
        privGeoMarktStorage.createPurchasePolicy(2.3);
    }).then(function(result){
        console.log("Created a purchase policy");
        console.log(result);
    })
    .catch(function(e) {
        // There was an error! Handle it.
        callback(e);
    });
};