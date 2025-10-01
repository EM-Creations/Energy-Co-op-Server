package uk.co.emcreations.energycoop.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.co.emcreations.energycoop.model.Site;
import uk.co.emcreations.energycoop.service.InfoService;

@Slf4j
@Service
@RequiredArgsConstructor
public class InfoServiceImpl implements InfoService {

    @Override
    public Site[] getSites() {
        return Site.values();
    }
}
