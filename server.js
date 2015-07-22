'use strict'

var express = require('express');
var http = require('http');
var morgan = require('morgan');
require('colors');
var app = express();
var server = http.createServer(app);

if (app.get('env') === 'development')
	app.use(morgan('dev'));
else
	app.use(morgan('combined'));

var paymentController = require('./controllers/payment');
paymentController(app);

app.set('port', 6797);

server.listen(app.get('port'), function() {
	console.log("listening on %d".green, app.get('port'));
});