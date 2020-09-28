package com.froggengo.practise.session;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

@RestController
@RequestMapping("/session")
public class MySession {

    @GetMapping("/redisSession/{name}")
    public Object setSession(@PathVariable("name") String name, HttpSession session){
        return session.getAttribute("name");
    }
    @GetMapping("/getRedisSession")
    public Object getSession(/*@PathVariable("name") String name, */HttpSession session){
        //System.out.println(session.getId());
        SessionInfo info = new SessionInfo();
        info.id=session.getId();
        info.creationTime=session.getCreationTime();
        info.lastAccessedTime =session.getLastAccessedTime();
        info.maxInactiveInterval=session.getMaxInactiveInterval();
        info.isNew=session.isNew();
        return info;
    }
    class SessionInfo {
        String id = "";
        long creationTime = 0L;
        long lastAccessedTime = 0L;
        int maxInactiveInterval = 0;
        boolean isNew = false;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public long getCreationTime() {
            return creationTime;
        }

        public void setCreationTime(long creationTime) {
            this.creationTime = creationTime;
        }

        public long getLastAccessedTime() {
            return lastAccessedTime;
        }

        public void setLastAccessedTime(long lastAccessedTime) {
            this.lastAccessedTime = lastAccessedTime;
        }

        public int getMaxInactiveInterval() {
            return maxInactiveInterval;
        }

        public void setMaxInactiveInterval(int maxInactiveInterval) {
            this.maxInactiveInterval = maxInactiveInterval;
        }

        public boolean isNew() {
            return isNew;
        }

        public void setNew(boolean aNew) {
            isNew = aNew;
        }
    }

}
