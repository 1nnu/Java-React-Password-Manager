package ee.taltech.passman.security.services;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import jakarta.servlet.http.HttpServletRequest;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import org.springframework.stereotype.Service;

@Service
public class LoginAttemptService {

  public static final int MAX_ATTEMPT = 10;
  private final LoadingCache<String, Integer> attemptsCache;

  private final HttpServletRequest request;

  public LoginAttemptService(HttpServletRequest request) {
    super();
    this.request = request;
    attemptsCache =
        CacheBuilder.newBuilder()
            .expireAfterWrite(1, TimeUnit.DAYS)
            .build(
                new CacheLoader<String, Integer>() {
                  @Override
                  public Integer load(final String key) {
                    return 0;
                  }
                });
  }

  public void loginFailed(final String key) {
    int attempts;
    try {
      attempts = attemptsCache.get(key);
    } catch (final ExecutionException e) {
      attempts = 0;
    }
    attempts++;
    attemptsCache.put(key, attempts);
  }

  public boolean isBlocked() {
    try {
      return attemptsCache.get(getClientIP()) >= MAX_ATTEMPT;
    } catch (final ExecutionException e) {
      return false;
    }
  }

  private String getClientIP() {
    final String xfHeader = request.getHeader("X-Forwarded-For");
    if (xfHeader != null) {
      return xfHeader.split(",")[0];
    }
    return request.getRemoteAddr();
  }
}
