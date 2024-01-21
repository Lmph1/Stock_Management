class Task extends Thread {
    private Callback callback;

    public Task(Callback callback) {
        this.callback = callback;
    }

    @Override
    public void run() {
        callback.execute(this);
    }
}
