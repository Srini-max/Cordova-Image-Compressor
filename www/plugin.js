
var exec = require('cordova/exec');

var PLUGIN_NAME = 'ImageCompressor';

var ImageCompressor = {
  compress: function(options, successCallback, errorCallback) {
    exec(successCallback, errorCallback, PLUGIN_NAME, 'compress', [options]);
  }
};

module.exports = ImageCompressor;
