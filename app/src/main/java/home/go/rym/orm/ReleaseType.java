package home.go.rym.orm;

public enum ReleaseType {
    ALBUM("album"),
    EP("ep"),
    COMPILATION("comp"),
    SINGLE("single"),
    BOOTLEG("unauth"),
    VIDEO("video");

    private String value;


    private ReleaseType(String value) {
        this.value = value;
    }
}
