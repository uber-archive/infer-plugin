class PassingInferExample {

    String mayReturnNull(int i) {
        return "Hello, Infer!";
    }

    int cantCauseNPE() {
        String s = mayReturnNull(0);
        return s.length();
    }
}
