package com.mar.ds.utils.jsonDialog.mapper;

import com.mar.ds.db.entity.Mission;
import com.mar.ds.db.entity.Task;
import com.mar.ds.db.jpa.TaskRepository;
import com.mar.ds.utils.jsonDialog.jsonData.MissionData;
import com.mar.ds.utils.jsonDialog.jsonData.TaskData;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.factory.Mappers;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedList;
import java.util.List;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface MissionMapper {

    TaskMapper taskMapper = Mappers.getMapper(TaskMapper.class);

    @Transactional
    default List<MissionData> getMissionData(List<Mission> missions, TaskRepository taskRepository) {
        if (isNull(missions)) return null;
        List<MissionData> res = new LinkedList<>();

        for (Mission mission : missions) {
            if (isNull(mission) || isNull(mission.getStartTask())) {
                break;
            }

            List<TaskData> taskDataList = new LinkedList<>();
            int order = 1;

            Task task = mission.getStartTask();
            TaskData taskData = taskMapper.getTaskData(task);
            taskData.setOrder(order++);
            taskDataList.add(taskData);

            Long nextTaskId = task.getAfterId();
            while (nonNull(nextTaskId)) {
                Task task1 = taskRepository.getById(nextTaskId);
                TaskData taskData1 = taskMapper.getTaskData(task1);
                taskData1.setOrder(order++);
                taskDataList.add(taskData1);
                nextTaskId = task1.getAfterId();
            }

            res.add(MissionData.builder()
                    .id(mission.getId())
                    .title(mission.getTitle())
                    .text(mission.getText())
                    .tasks(taskDataList)
                    .build()
            );
        }

        return res;
    }

}
