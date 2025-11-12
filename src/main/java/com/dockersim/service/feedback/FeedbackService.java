package com.dockersim.service.feedback;

import com.dockersim.dto.request.DockerfileFeedbackRequest;
import com.dockersim.dto.response.DockerfileFeedbackResponse;

public interface FeedbackService {
    DockerfileFeedbackResponse getDockerfileFeedback(DockerfileFeedbackRequest request);
}
