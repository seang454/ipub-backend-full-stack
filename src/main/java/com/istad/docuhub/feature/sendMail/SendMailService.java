package com.istad.docuhub.feature.sendMail;

import com.istad.docuhub.feature.sendMail.dto.SendMailRequest;
import com.istad.docuhub.feature.sendMail.dto.SendMailResponse;

public interface SendMailService {
    SendMailResponse sendMailReject(SendMailRequest sendMailRequest);
}
