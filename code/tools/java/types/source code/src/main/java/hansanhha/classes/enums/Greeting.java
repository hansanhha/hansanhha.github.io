package hansanhha.classes.enums;

public enum Greeting {

    KOREAN {
        @Override
        public String greet() {
            return "안녕";
        }
    },

    ENGLISH {
        @Override
        public String greet() {
            return "hello";
        }
    },

    JAPANESE {
        @Override
        public String greet() {
            return "こんにちは";
        }
    };

    abstract public String greet();
}
