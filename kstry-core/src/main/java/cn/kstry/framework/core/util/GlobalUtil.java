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
package cn.kstry.framework.core.util;

import cn.kstry.framework.core.bpmn.FlowElement;
import cn.kstry.framework.core.bpmn.enums.BpmnTypeEnum;
import cn.kstry.framework.core.constant.GlobalProperties;
import cn.kstry.framework.core.engine.facade.StoryRequest;
import cn.kstry.framework.core.exception.ExceptionEnum;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;
import org.slf4j.helpers.MessageFormatter;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * GlobalUtil
 *
 * @author lykan
 */
public class GlobalUtil {

    @SuppressWarnings("all")
    public static <T> T notEmpty(Optional<T> optional) {
        if (!optional.isPresent()) {
            throw ExceptionUtil.buildException(null, ExceptionEnum.NOT_ALLOW_EMPTY, null);
        }
        return optional.get();
    }

    public static <T> T notNull(T obj) {
        AssertUtil.notNull(obj);
        return obj;
    }

    public static String notBlank(String str) {
        AssertUtil.notBlank(str);
        return str;
    }

    public static String getTaskName(FlowElement flowElement, String requestId) {
        AssertUtil.notNull(flowElement);
        return "tid-" + TaskServiceUtil.joinName(flowElement.getId(), requestId);
    }

    public static String format(String str, Object... params) {
        if (StringUtils.isBlank(str)) {
            return str;
        }
        return MessageFormatter.arrayFormat(str, params).getMessage();
    }

    @SuppressWarnings("unchecked")
    public static <T> Optional<T> transfer(Object source, Class<T> targetClass) {
        AssertUtil.notNull(targetClass);
        if (source == null) {
            return Optional.empty();
        }
        AssertUtil.isTrue(ElementParserUtil.isAssignable(targetClass, source.getClass()), ExceptionEnum.TYPE_TRANSFER_ERROR,
                "{} expect: {}, actual: {}", ExceptionEnum.TYPE_TRANSFER_ERROR.getDesc(), targetClass.getName(), source.getClass().getName());
        return GlobalUtil.resOptional((T) source);
    }

    public static <T> T transferNotEmpty(Object source, Class<T> targetClass) {
        return notEmpty(transfer(source, targetClass));
    }

    public static boolean isCollection(Class<?> clazz) {
        if (clazz == null) {
            return false;
        }
        return Iterable.class.isAssignableFrom(clazz) || Map.class.isAssignableFrom(clazz) || clazz.isArray();
    }

    /**
     * 排序后子类在前，父类在后
     */
    @SuppressWarnings("unchecked")
    public static <T> List<T> sortObjExtends(Collection<T> objList) {
        if (CollectionUtils.isEmpty(objList)) {
            return Lists.newArrayList();
        }
        Object[] objs = objList.stream().filter(Objects::nonNull).toArray();
        if (objs.length < 2) {
            return Lists.newArrayList(objList);
        }

        boolean isContinue;
        do {
            isContinue = false;
            for (int i = 0; i < objs.length; i++) {
                for (int j = i; j < objs.length; j++) {
                    if (objs[i].getClass() == objs[j].getClass()) {
                        continue;
                    }
                    if (objs[i].getClass().isAssignableFrom(objs[j].getClass())) {
                        Object o = objs[i];
                        objs[i] = objs[j];
                        objs[j] = o;
                        isContinue = true;
                    }
                }
            }
        } while (isContinue);
        return Arrays.stream(objs).map(t -> (T) t).collect(Collectors.toList());
    }

    public static String getOrSetRequestId(StoryRequest<?> storyRequest) {
        if (StringUtils.isNotBlank(storyRequest.getRequestId())) {
            return storyRequest.getRequestId();
        }

        String requestId = MDC.get(GlobalProperties.KSTRY_STORY_REQUEST_ID_NAME);
        if (StringUtils.isBlank(requestId)) {
            requestId = GlobalUtil.uuid();
        }
        storyRequest.setRequestId(requestId);
        return requestId;
    }

    public static void traceIdClear(String oldReqId, String reqLogIdKey) {
        if (StringUtils.isNotBlank(oldReqId)) {
            MDC.put(reqLogIdKey, oldReqId);
        } else {
            MDC.remove(reqLogIdKey);
        }
    }

    public static String expToString(Throwable e) {
        AssertUtil.notNull(e);
        StringWriter writer = new StringWriter();
        e.printStackTrace(new PrintWriter(writer, true));
        return writer.toString();
    }

    public static String uuid() {
        return UUID.randomUUID().toString().replace("-", StringUtils.EMPTY);
    }

    public static String uuid(BpmnTypeEnum typeEnum) {
        if (typeEnum == null) {
            return uuid();
        }
        return typeEnum.name() + "-" + uuid();
    }

    public static String uuid(BpmnTypeEnum typeEnum, String baseId) {
        if (typeEnum == null || StringUtils.isBlank(baseId)) {
            return uuid();
        }
        return typeEnum.name() + "-" + baseId + "-" + uuid();
    }

    @SuppressWarnings("unchecked")
    public static <T> Optional<T> resOptional(T res) {
        return (res instanceof Optional) ? (Optional<T>) res : Optional.ofNullable(res);
    }

    public static String getOriginalId(boolean needOriginalId, String elementId) {
        if (!needOriginalId) {
            return elementId;
        }
        if (StringUtils.isBlank(elementId)) {
            return elementId;
        }
        return elementId.replaceAll("-\\d+$", StringUtils.EMPTY);
    }
}
