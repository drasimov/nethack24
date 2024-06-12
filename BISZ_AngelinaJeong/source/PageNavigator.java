

// Toggle page between GPAPage and SubjectPage
enum PageNavigateType {
	GPAPAGE_TO_SUBJECTPAGE,
	SUBJECTPAGE_TO_GPAPAGE_BY_SAVE_BUTTON,
	SUBJECTPAGE_TO_GPAPAGE_BY_CANCEL_BUTTON,
}


interface PageNavigator {
	public void navigate(PageNavigateType type);
}
