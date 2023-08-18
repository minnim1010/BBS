export default class Post {
  title;
  content;
  category;

  constructor(initialPost) {
    this.title = initialPost.title ? initialPost.title : "";
    this.content = initialPost.content ? initialPost.content : "";
    this.category = initialPost.category ? initialPost.category : "";
  }
}
