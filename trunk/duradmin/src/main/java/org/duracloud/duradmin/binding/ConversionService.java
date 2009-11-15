
package org.duracloud.duradmin.binding;

import org.springframework.binding.convert.service.DefaultConversionService;

public class ConversionService
        extends DefaultConversionService {

    @Override
    protected void addDefaultConverters() {
        super.addDefaultConverters();
        addConverter(new StringToMultipartFile());
    }
}
