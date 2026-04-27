package service.implementation.cv;

import service.interfaces.internal.CVTierClassifierService;

public class CVTierClassifierServiceImpl implements CVTierClassifierService {
    @Override
    public char classifyCV(int cv) {
        if (cv > 70){
            return 'A';
        }
        if (cv > 50){
            return 'B';
        }

        return 'C';

    }
}
