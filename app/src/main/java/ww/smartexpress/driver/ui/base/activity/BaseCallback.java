package ww.smartexpress.driver.ui.base.activity;

public interface BaseCallback {
    void doError(Throwable error);

    void doSuccess();

    void doFail();
}
