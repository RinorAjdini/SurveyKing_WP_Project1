    package finki.ukim.mk.surveyKing.web;

    import finki.ukim.mk.surveyKing.model.*;
    import finki.ukim.mk.surveyKing.service.OptionService;
    import finki.ukim.mk.surveyKing.service.PollService;
    import finki.ukim.mk.surveyKing.service.QuestionService;
    import finki.ukim.mk.surveyKing.service.UserService;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.security.access.prepost.PreAuthorize;
    import org.springframework.security.core.Authentication;
    import org.springframework.stereotype.Controller;
    import org.springframework.ui.Model;
    import org.springframework.web.bind.annotation.*;

    import java.util.*;
    import java.util.stream.Collectors;

    @Controller
    public class PollController {

        @Autowired
        private PollService pollService;

        @Autowired
        private QuestionService questionService;

        @Autowired
        private UserService userService;

        @Autowired
        private OptionService optionService;

        @GetMapping("/")
        public String homePage(@RequestParam(name = "sort", required = false) String sort, Model model, Authentication authentication) {
            User user = userService.findByUsername(authentication.getName());
            Set<Poll> completedPolls = user.getCompletedPolls();
            List<Poll> allPolls;

            // Apply sorting based on the 'sort' parameter
            if ("likesAsc".equals(sort)) {
                allPolls = pollService.getAllPollsSortedByLikesAsc();
            } else if ("likesDesc".equals(sort)) {
                allPolls = pollService.getAllPollsSortedByLikesDesc();
            } else if ("createdAtAsc".equals(sort)) {
                allPolls = pollService.getAllPollsSortedByCreatedAtAsc();
            } else if ("createdAtDesc".equals(sort)) {
                allPolls = pollService.getAllPollsSortedByCreatedAtDesc();
            } else {
                allPolls = pollService.getAllPols(); // Default case: no sorting
            }

            // Filter out polls that the user has already completed
            List<Poll> availablePolls = allPolls.stream()
                    .filter(poll -> !completedPolls.contains(poll))
                    .collect(Collectors.toList());

            // Prepare the like status map
            Map<Integer, Boolean> pollLikeStatus = availablePolls.stream()
                    .collect(Collectors.toMap(Poll::getId, poll -> poll.getLikedUsers().contains(user)));

            model.addAttribute("polls", availablePolls);
            model.addAttribute("currentUsername", user.getUsername());
            model.addAttribute("pollLikeStatus", pollLikeStatus); // Add the like status map to the model
            model.addAttribute("sort", sort); // Add the sort parameter to the model

            return "index";
        }
        @GetMapping("/participated-polls")
        public String participatedPolls(Model model, Authentication authentication) {
            User user = userService.findByUsername(authentication.getName());
            Set<Poll> completedPolls = user.getCompletedPolls();

            model.addAttribute("polls", completedPolls);
            model.addAttribute("currentUsername", user.getUsername());
            return "participated_polls";
        }
        @GetMapping("/my-surveys")
        public String viewMySurveys(Model model, Authentication authentication) {
            User user = userService.findByUsername(authentication.getName());

            // Fetch surveys created by the logged-in user
            List<Poll> mySurveys = pollService.getPollsByUser(user);

            Map<Integer, Integer> participantsCountMap = new HashMap<>();

            // Calculate the number of participants for each survey
            for (Poll poll : mySurveys) {
                int count = pollService.getParticipantCount(poll.getId());
                participantsCountMap.put(poll.getId(), count);
            }

            model.addAttribute("polls", mySurveys);
            model.addAttribute("participantsCountMap", participantsCountMap);
            model.addAttribute("currentUsername", user.getUsername());
            return "my_surveys";
        }
        @GetMapping("/search")
        public String searchPolls(@RequestParam("query") String query, Model model, Authentication authentication) {
            List<Poll> polls = pollService.searchPollsByName(query);
            model.addAttribute("polls", polls);

            if (authentication != null) {
                User user = userService.findByUsername(authentication.getName());
                model.addAttribute("currentUsername", user.getUsername());

                // Compute the like status for each poll in the search result
                Map<Integer, Boolean> pollLikeStatus = polls.stream()
                        .collect(Collectors.toMap(Poll::getId, poll -> poll.getLikedUsers().contains(user)));
                model.addAttribute("pollLikeStatus", pollLikeStatus);
            }

            return "index";
        }
    //    @GetMapping("/poll/{id}")
    //    public String viewPoll(@PathVariable int id, Model model) {
    //        Poll poll = pollService.getPollById(id);
    //        model.addAttribute("poll", poll);
    //        model.addAttribute("questions", poll.getQuestions());
    //        return "poll";
    //    }
    @GetMapping("/poll/{id}")
    public String viewPoll(@PathVariable int id, Model model, Authentication authentication) {
        Poll poll = pollService.getPollById(id);
        User user = userService.findByUsername(authentication.getName());

        List<Option> selectedOptions = optionService.getSelectedOptionsByPollAndUser(poll, user);

        model.addAttribute("poll", poll);
        model.addAttribute("questions", poll.getQuestions());
        model.addAttribute("selectedOptions", selectedOptions);

        return "poll";
    }

        @GetMapping("/poll/create")
        public String createPollForm(Model model) {
            model.addAttribute("poll", new Poll());
            return "create_poll";
        }

        @PostMapping("/poll/create")
        public String createPoll(@ModelAttribute PollData pollData, Model model, Authentication authentication) {
            if (pollData.getName() == null || pollData.getName().trim().isEmpty()) {
                model.addAttribute("error", "Poll name is required");
                return "create_poll";
            }

            if (pollData.getQuestions() == null || pollData.getQuestions().isEmpty()) {
                model.addAttribute("error", "At least one question is required");
                return "create_poll";
            }

            Poll poll = new Poll();
            poll.setName(pollData.getName());
            poll.setCreatedAt(new Date());
            List<Question> questionList = new ArrayList<>();
            for (int i = 0; i < pollData.getQuestions().size(); i++) {
                QuestionData questionData = pollData.getQuestions().get(i);
                Question question = new Question();
                question.setText(questionData.getText());
                question.setOrderIndex(i);
                question.setPoll(poll);

                List<Option> optionList = new ArrayList<>();
                for (int j = 0; j < questionData.getOptions().size(); j++) {
                    OptionData optionData = questionData.getOptions().get(j);
                    Option option = new Option();
                    option.setDescription(optionData.getDescription());
                    option.setOrderIndex(j);
                    option.setQuestion(question);
                    option.setPoll(poll);
                    optionList.add(option);
                }
                question.setOptions(optionList);
                questionList.add(question);
            }
            poll.setQuestions(questionList);

            User user = userService.findByUsername(authentication.getName());
            poll.setUser(user);
            pollService.createPoll(poll);

            return "redirect:/";
        }


    //    @PostMapping("/vote")
    //    public String createVote(@RequestParam("selectedOptions") List<Integer> selectedOptionIds,
    //                             @RequestParam("pollId") int pollId,
    //                             Authentication authentication,
    //                             Model model) {
    //
    //        User user = userService.findByUsername(authentication.getName());
    //        Poll poll = pollService.getPollById(pollId);
    //
    //        if (user.getCompletedPolls().contains(poll)) {
    //            model.addAttribute("error", "You have already participated in this poll.");
    //            return "error"; // Return an error page or redirect as needed
    //        }
    //
    //        for (Integer optionId : selectedOptionIds) {
    //            optionService.incrementVote(optionId, user);
    //        }
    //
    //        // Add the poll to the user's completed polls
    //        user.getCompletedPolls().add(poll);
    //        userService.save(user); // Ensure you have a save method in your UserService
    //
    //        return "redirect:/participated-polls";
    //    }
    @PostMapping("/vote")
    public String createOrUpdateVote(@RequestParam("selectedOptions") List<Integer> selectedOptionIds,
                                     @RequestParam("pollId") int pollId,
                                     Authentication authentication,
                                     Model model) {

        User user = userService.findByUsername(authentication.getName());
        Poll poll = pollService.getPollById(pollId);

        if (user.getCompletedPolls().contains(poll)) {
            optionService.deleteSelectedOptionsByPollAndUser(poll, user);
        }

        for (Integer optionId : selectedOptionIds) {
            optionService.incrementVote(optionId, user);
        }

        user.getCompletedPolls().add(poll);
        userService.save(user);

        return "redirect:/participated-polls";
    }
        @PreAuthorize("hasRole('ADMIN') or @pollSecurityService.isPollOwner(authentication.principal.username, #id)")
        @GetMapping("/poll/{id}/results")
        public String pollResults(@PathVariable int id, Model model) {
            Poll poll = pollService.getPollById(id);
            int participatCount= pollService.getParticipantCount(id);
            for (Question question : poll.getQuestions()) {
                int totalVotes = question.getOptions().stream().mapToInt(Option::getVotes).sum();
                for (Option option : question.getOptions()) {
                    if (totalVotes > 0) {
                        double percentage = ((double) option.getVotes() / totalVotes) * 100;
                        option.setPercentage(Double.parseDouble(String.format("%.2f", percentage)));
                    } else {
                        option.setPercentage(Double.parseDouble("0.00"));
                    }
                }
            }
            model.addAttribute("poll", poll);
            model.addAttribute("participantCount",participatCount);
            return "results";
        }

        @PostMapping("/poll/{id}/like")
        public String likePoll(@PathVariable int id, Authentication authentication) {
            String username = authentication.getName();
            User user = userService.findByUsername(username);

            if (user != null) {
                pollService.likePoll(id, user.getId());
            } else {
                return "redirect:/login";
            }

            return "redirect:/"; // Redirect to the home page or wherever you want
        }

        @GetMapping("/poll/{id}/my-submission")
        public String viewMySubmission(@PathVariable("id") int pollId, Authentication authentication, Model model) {
            // Retrieve the poll by ID
            Poll poll = pollService.getPollById(pollId);

            String username = authentication.getName();
            User user = userService.findByUsername(username);

            List<Option> selectedOptions = optionService.getSelectedOptionsByPollAndUser(poll, user);

            model.addAttribute("poll", poll);
            model.addAttribute("selectedOptions", selectedOptions);

            return "my_submission";
        }

        @PostMapping("/poll/{id}/delete-submission")
        public String deleteSubmission(@PathVariable("id") int pollId, Authentication authentication) {
            String username = authentication.getName();
            User user = userService.findByUsername(username);

            Poll poll = pollService.getPollById(pollId);

            List<Option> selectedOptions = optionService.getSelectedOptionsByPollAndUser(poll, user);

            // Decrement the vote count for each selected option
            for (Option option : selectedOptions) {
                optionService.decrementVote(option.getId(), user);
            }

            // Remove user associations with the options after decrementing the votes
            optionService.deleteSelectedOptionsByPollAndUser(poll, user);

            user.getCompletedPolls().remove(poll);
            userService.save(user);

            return "redirect:/participated-polls";
        }
        @PreAuthorize("hasRole('ADMIN') or @pollSecurityService.isPollOwner(authentication.principal.username, #id)")
        @GetMapping("/poll/edit/{id}")
        public String editPollForm(@PathVariable int id, Model model) {
            Poll poll = pollService.getPollById(id);
            model.addAttribute("poll", poll);
            return "edit_poll";
        }

        @PostMapping("/poll/edit/{id}")
        public String editPoll(@PathVariable int id, @ModelAttribute PollData pollData) {
            Poll poll = pollService.getPollById(id);
            pollService.updatePoll(poll, pollData);
            return "redirect:/";
        }

        @PreAuthorize("hasRole('ADMIN') or @pollSecurityService.isPollOwner(authentication.principal.username, #id)")
        @PostMapping("/poll/delete/{id}")
        public String deletePoll(@PathVariable int id) {
            pollService.deletePoll(id);
            return "redirect:/";
        }
    }

