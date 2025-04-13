package team.backend.curio.dto;

public class UserCreateDto {
    private String socialType; // 카카오 or 구글
    private String newsletterEmail; // 뉴스레터 이메일

    // 생성자, getter, setter

    public UserCreateDto(String socialType, String newsletterEmail) {
        this.socialType = socialType;
        this.newsletterEmail = newsletterEmail;
    }

    public String getSocialType() {
        return socialType;
    }

    public void setSocialType(String socialType) {
        this.socialType = socialType;
    }

    public String getNewsletterEmail() {
        return newsletterEmail;
    }

    public void setNewsletterEmail(String newsletterEmail) {
        this.newsletterEmail = newsletterEmail;
    }

}
