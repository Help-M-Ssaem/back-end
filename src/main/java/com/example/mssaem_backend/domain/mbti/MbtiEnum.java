package com.example.mssaem_backend.domain.mbti;

public enum MbtiEnum {
    INFJ("https://mssaem-bucket.s3.ap-northeast-2.amazonaws.com/M-ssaem/3070b9d0-0658-4c9b-b34a-47320350a1d5.png"),
    INFP("https://mssaem-bucket.s3.ap-northeast-2.amazonaws.com/M-ssaem/a88cb201-17ce-4f58-9e6c-3ee526af4ad7.png"),
    ISFJ("https://mssaem-bucket.s3.ap-northeast-2.amazonaws.com/M-ssaem/f23354e8-7710-42a3-af2e-c4a3ea27222e.png"),
    ISFP("https://mssaem-bucket.s3.ap-northeast-2.amazonaws.com/M-ssaem/2aa65ede-6304-44f0-a16e-5e228b597154.png"),
    ISTP("https://mssaem-bucket.s3.ap-northeast-2.amazonaws.com/M-ssaem/0b30af0f-628e-4d6e-a2ef-fad0ff06d650.png"),
    ISTJ("https://mssaem-bucket.s3.ap-northeast-2.amazonaws.com/M-ssaem/ab75f543-2f73-4095-8308-c71394e84b33.png"),
    INTP("https://mssaem-bucket.s3.ap-northeast-2.amazonaws.com/M-ssaem/5335e604-efca-4afc-8298-e5905b0f68b4.png"),
    INTJ("https://mssaem-bucket.s3.ap-northeast-2.amazonaws.com/M-ssaem/a4b75267-0599-4617-a037-8a531a18a24e.png"),
    ENTP("https://mssaem-bucket.s3.ap-northeast-2.amazonaws.com/M-ssaem/7321c26b-e29c-4763-bdeb-2f5de57eed2d.png"),
    ESTJ("https://mssaem-bucket.s3.ap-northeast-2.amazonaws.com/M-ssaem/d22f199a-2b2a-410e-a4b5-ca51f86459ee.png"),
    ESTP("https://mssaem-bucket.s3.ap-northeast-2.amazonaws.com/M-ssaem/0d3acd01-aedd-486a-bb88-6645319959eb.png"),
    ENFP("https://mssaem-bucket.s3.ap-northeast-2.amazonaws.com/M-ssaem/309c8545-9063-4245-aba9-17cbf89f4e6f.png"),
    ESFJ("https://mssaem-bucket.s3.ap-northeast-2.amazonaws.com/M-ssaem/fef3e04f-f7f2-4ddb-9bf5-08d6c5a6a4b8.png"),
    ENTJ("https://mssaem-bucket.s3.ap-northeast-2.amazonaws.com/M-ssaem/entj.png"),
    ENFJ("https://mssaem-bucket.s3.ap-northeast-2.amazonaws.com/M-ssaem/bea7ba0f-6d9e-45d2-8d3e-2a219a9b3aa2.png"),
    ESFP("https://mssaem-bucket.s3.ap-northeast-2.amazonaws.com/M-ssaem/f440d2f9-7d06-4ca7-a907-7598b173fa3e.png");

    private final String profileUrl;

    MbtiEnum(String profileUrl) {
        this.profileUrl = profileUrl;
    }

    public String getProfileUrl() { return profileUrl; }
}
