package util;


import java.util.List;

public interface CodeCallbackListener {
    void onFinish(List<String> id, List<String> name);
    void onError(Exception e);
}
