package team.backend.curio.domain;

import java.io.Serializable;
import java.util.Objects;

 /* UserAction의 복합키 클래스
 * userId + newsId 조합이 고유한 키가 됨
 */
public class UserActionId implements Serializable{
    public Long userId;
    public Long newsId;

    public UserActionId() {} // 기본 생성자 (JPA용)

    public UserActionId(Long userId, Long newsId){
        this.userId=userId;
        this.newsId=newsId;
    }

    @Override
     public boolean equals(Object o){
        if(this==o) return true;
        if (!(o instanceof UserActionId)) return false;
        UserActionId that = (UserActionId) o;
        return Objects.equals(userId, that.userId) &&
                Objects.equals(newsId, that.newsId);
    }

     @Override
     public int hashCode() {
         return Objects.hash(userId, newsId);
     }
}
