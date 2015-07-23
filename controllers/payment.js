'use strict'

module.exports = function paymentController(app) {

    var response = "Payed";

    app.get('/payment', function (req, res) {
        var sleep = (Math.random() * 10) + 1;
        setTimeout(function () {
            res.send(response);
        }, sleep * 1000);
        console.log("waiting " + sleep + " seconds")

    });

};