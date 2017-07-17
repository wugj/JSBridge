(function(){

	if (window.WebViewJavascriptBridge) {
        return;
    }
	var messageingIframe;
	var sendMessageQueueArr = [];
	var successCallBackObj = {};
	var errorCallBackObj = {};
	var uniqueId = 1;

	 var NM_PROTOCOL_SCHEME = 'nybjs';
    var QUEUE_HAS_MESSAGE = '__queue_message__/';

    function createIfream(doc){
		messageingIframe = doc.createElement('iframe');
		messageingIframe.style.display = 'none';
		doc.documentElement.appendChild(messageingIframe);
	}

	function send(handlerName,data,successCallBack,errorCallBack){
		doSend({
			handlerName: handlerName,
			data: data
		},successCallBack,errorCallBack);
	}

	function doSend(message,successCallBack,errorCallBack){


		var suucessCallbackId = "nm_" + (uniqueId++) + "_" + (new Date()).getTime();
		var errorCallbackId = "nm_" + (uniqueId++) + "_" + (new Date()).getTime();

		if (successCallBack) {
			successCallBackObj[suucessCallbackId] = successCallBack;
			message.successCallbackId = suucessCallbackId;
		}
		if (errorCallBack) {
			errorCallBackObj[errorCallbackId] = errorCallBack;
			message.errorCallbackId = errorCallbackId;
		};
		sendMessageQueueArr.push(message);

		messageingIframe.src = NM_PROTOCOL_SCHEME + "://" + QUEUE_HAS_MESSAGE;
		// alert(messageingIframe.src);
	}

	function fetchMessageQueue(){
		var messageQueueString = JSON.stringify(sendMessageQueueArr);
		sendMessageQueueArr = [];
		// alert(NM_PROTOCOL_SCHEME + '://return/fetchMessageQueue/' + encodeURIComponent(messageQueueString));
		messageingIframe.src = NM_PROTOCOL_SCHEME + '://return/fetchMessageQueue/' + encodeURIComponent(messageQueueString);
	}

	function handleMessageFromNative(messageJSON){
		dispatchMessageFromNative(messageJSON);
	}

	function dispatchMessageFromNative(messageJSON){
		setTimeout(function(){
			var message = JSON.parse(messageJSON);
			// alert(messageJSON);
			// var message = messageJSON;

			var responseCallBack;
			if (message.responseId) {
				responseCallBack = successCallBackObj[message.responseId];
				if (!responseCallBack) {
					responseCallBack = errorCallBackObj[message.responseId];
				};
				if (!responseCallBack) {
					return;
				};
				var responseJson = JSON.parse(message.responseData);

				responseCallBack(responseJson);

				delete successCallBackObj[message.successCallbackId];
				delete errorCallBackObj[message.errorCallbackId];
			};
		});
	}

	 window.WebViewJavascriptBridge = {
	 	send:send,
	 	fetchMessageQueue:fetchMessageQueue,
	 	handleMessageFromNative:handleMessageFromNative
	 };

   window.HKWidget = {
        picker : function(params, onSuccess, onError){
            window.WebViewJavascriptBridge.send("picker", params, onSuccess, onError);
        }
    };

	createIfream(document);

	 if (window.onBridgeReady) {
            window.onBridgeReady();
      }

})();