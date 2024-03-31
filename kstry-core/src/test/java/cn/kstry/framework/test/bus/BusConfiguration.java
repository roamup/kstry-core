/*
 *
 *  * Copyright (c) 2020-2024, Lykan (jiashuomeng@gmail.com).
 *  * <p>
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  * <p>
 *  *     http://www.apache.org/licenses/LICENSE-2.0
 *  * <p>
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */
package cn.kstry.framework.test.bus;

import cn.kstry.framework.core.annotation.EnableKstry;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author lykan
 */
@Configuration
@EnableKstry(bpmnPath = "./bpmn/bus/*.bpmn")
@PropertySource("classpath:application.properties")
@ComponentScan(basePackageClasses = BusConfiguration.class)
public class BusConfiguration {

    @Bean(name = "custom-mmm")
    public ThreadPoolExecutor executor() {
        return new ThreadPoolExecutor(
                5,
                10,
                10,
                TimeUnit.MILLISECONDS,
                new ArrayBlockingQueue<>(3),
                new ThreadFactoryBuilder().setNameFormat("custom-mmm-%d").build(),
                new ThreadPoolExecutor.AbortPolicy());
    }
}
