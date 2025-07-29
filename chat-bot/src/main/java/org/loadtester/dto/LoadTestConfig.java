package org.loadtester.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class LoadTestConfig {
    private int userCount;
    private int rampUpTimeSeconds;
    private int chatDurationSeconds;
    private double messageRatePerUser;
    private int messageLength;
    private String restApiBaseUrl;
    private String webSocketEndpoint;
}
