package jt.com.jsbridge.view.jsbridge;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Message {

	private String successCallbackId; // callbackId
	private String errorCallbackId;
	private String responseId; // responseId
	private String responseData; // responseData
	private String data; // data of message
	private String handlerName; // name of handler

	private final static String SUCCESSS_CALLBACK_ID_STR = "successCallbackId";
	private final static String ERROR_CALLBACK_ID_STR = "errorCallbackId";
	private final static String RESPONSE_ID_STR = "responseId";
	private final static String RESPONSE_DATA_STR = "responseData";
	private final static String DATA_STR = "data";
	private final static String HANDLER_NAME_STR = "handlerName";
	
	public String getResponseId() {
		return responseId;
	}
	public void setResponseId(String responseId) {
		this.responseId = responseId;
	}
	public String getResponseData() {
		return responseData;
	}
	public void setResponseData(String responseData) {
		this.responseData = responseData;
	}
	public String getData() {
		return data;
	}
	public void setData(String data) {
		this.data = data;
	}
	public String getHandlerName() {
		return handlerName;
	}
	public void setHandlerName(String handlerName) {
		this.handlerName = handlerName;
	}
	public String getSuccessCallbackId() {
		return successCallbackId;
	}
	public void setSuccessCallbackId(String successCallbackId) {
		this.successCallbackId = successCallbackId;
	}
	public String getErrorCallbackId() {
		return errorCallbackId;
	}
	public void setErrorCallbackId(String errorCallbackId) {
		this.errorCallbackId = errorCallbackId;
	}
	public String toJson(){
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put(SUCCESSS_CALLBACK_ID_STR, successCallbackId);
			jsonObject.put(ERROR_CALLBACK_ID_STR, errorCallbackId);
			jsonObject.put(RESPONSE_ID_STR, responseId);
			jsonObject.put(RESPONSE_DATA_STR, responseData);
			jsonObject.put(DATA_STR, data);
			jsonObject.put(HANDLER_NAME_STR, handlerName);
			return jsonObject.toString();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static Message toObject(String jsonStr) {
		Message m =  new Message();
        try {
            JSONObject jsonObject = new JSONObject(jsonStr);
            m.setHandlerName(jsonObject.has(HANDLER_NAME_STR) ? jsonObject.getString(HANDLER_NAME_STR):null);
            m.setSuccessCallbackId(jsonObject.has(SUCCESSS_CALLBACK_ID_STR) ? jsonObject.getString(SUCCESSS_CALLBACK_ID_STR):null);
            m.setErrorCallbackId(jsonObject.has(ERROR_CALLBACK_ID_STR) ? jsonObject.getString(ERROR_CALLBACK_ID_STR):null);
            m.setResponseData(jsonObject.has(RESPONSE_DATA_STR) ? jsonObject.getString(RESPONSE_DATA_STR):null);
            m.setResponseId(jsonObject.has(RESPONSE_ID_STR) ? jsonObject.getString(RESPONSE_ID_STR):null);
            m.setData(jsonObject.has(DATA_STR) ? jsonObject.getString(DATA_STR):null);
            return m;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return m;
	}
	
	public static List<Message> toArrayList(String jsonStr){
        List<Message> list = new ArrayList<Message>();
        try {
            JSONArray jsonArray = new JSONArray(jsonStr);
            for(int i = 0; i < jsonArray.length(); i++){
                Message m = new Message();
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                m.setHandlerName(jsonObject.has(HANDLER_NAME_STR) ? jsonObject.getString(HANDLER_NAME_STR):null);
                m.setSuccessCallbackId(jsonObject.has(SUCCESSS_CALLBACK_ID_STR) ? jsonObject.getString(SUCCESSS_CALLBACK_ID_STR):null);
                m.setErrorCallbackId(jsonObject.has(ERROR_CALLBACK_ID_STR) ? jsonObject.getString(ERROR_CALLBACK_ID_STR):null);
                m.setResponseData(jsonObject.has(RESPONSE_DATA_STR) ? jsonObject.getString(RESPONSE_DATA_STR):null);
                m.setResponseId(jsonObject.has(RESPONSE_ID_STR) ? jsonObject.getString(RESPONSE_ID_STR):null);
                m.setData(jsonObject.has(DATA_STR) ? jsonObject.getString(DATA_STR):null);
                list.add(m);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
	}
}
