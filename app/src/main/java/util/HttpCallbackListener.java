package util;



public interface HttpCallbackListener {
//	void onFinish(List<String> id, List<String> name);
	void onFinish(String response);
	void onError(Exception e);
}
