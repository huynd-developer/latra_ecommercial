package trungdevcode.latra.modules.user.service;

import java.util.Map;

public interface UserService {
    Map<String, Object> getCurrentUserProfile(String username);
}