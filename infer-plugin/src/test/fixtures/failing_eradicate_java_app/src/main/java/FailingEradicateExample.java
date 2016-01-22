import javax.annotation.Generated;

class PassingEradicateExample {

    String mayReturnNull() {
        return null;
    }

    int cantCauseNPE() {
        String s = mayReturnNull();
        return s.length();
    }
}
