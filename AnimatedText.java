class AnimatedText {

    void animate(String text) {
        for(int i = 0 ; i < length(text); i++) {
            print(charAt(text, i));
            sleep(100);
        }
        print("\n");
    }

    void animate(String text, int millisCooldown) {
        for(int i = 0 ; i < length(text); i++) {
            print(charAt(text, i));
            sleep(millisCooldown);
        }
        print("\n");
    }

}