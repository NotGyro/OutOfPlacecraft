package ink.echol.outofplacecraft.capabilities;

public class YingletStatus implements IYingletStatus {
    public YingletStatus(boolean value) {
        ying = value;
    }

    private boolean ying = false;
    @Override
    public boolean isYinglet() {
        return ying;
    }

    @Override
    public void setIsYinglet(boolean value) {
        ying = value; //Dare you enter my magical realm?
    }
}
