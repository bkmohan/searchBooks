package android.apps.com.books;

import android.graphics.Bitmap;

import java.util.List;

public class Book {
    private String title;
    private List<String> authors;
    private String publisher;
    private String publishedDate;
    private float ratings;
    private String description;
    private List<String> identifiers;
    private String previewLink;
    private int pageCount;
    private Bitmap thumbnail = null;
    private String imageLink;

    private Book() {
    }

    public String getImageLink() {
        return imageLink;
    }

    public Bitmap getThumbnail() {
        return thumbnail;
    }

    public String getTitle() {
        return title;
    }

    public List<String> getAuthors() {
        return authors;
    }

    public String getPublisher() {
        return publisher;
    }

    public String getPublishedDate() {
        return publishedDate;
    }

    public float getRatings() {
        return ratings;
    }

    public String getDescription() {
        return description;
    }

    public List<String> getIdentifiers() {
        return identifiers;
    }

    public String getPreviewLink() {
        return previewLink;
    }

    public int getPageCount() {
        return pageCount;
    }

    public static class Builder {
        private String title;
        private List<String> authors;
        private String publisher;
        private String publishedDate;
        private float ratings;
        private String description;
        private List<String> identifiers;
        private String imageLink;
        private String previewLink;
        private int pageCount;
        private Bitmap thumbnail;

        public Builder(String title) {
            this.title = title;
        }

        public Builder addAuthors(List<String> authors) {
            this.authors = authors;
            return this;
        }

        public Builder addImageLink(String imageLink) {
            this.imageLink = imageLink;
            return this;
        }

        public Builder addPublisher(String publisher) {
            this.publisher = publisher;
            return this;
        }

        public Builder addPublishedDate(String publishedDate) {
            this.publishedDate = publishedDate;
            return this;
        }

        public Builder addratings(float ratings) {
            this.ratings = ratings;
            return this;
        }

        public Builder adddescription(String description) {
            this.description = description;
            return this;
        }

        public Builder addidentifiers(List<String> identifiers) {
            this.identifiers = identifiers;
            return this;
        }

        public Builder addpageCount(int pageCount) {
            this.pageCount = pageCount;
            return this;
        }

        public Builder addpreviewLink(String previewLink) {
            this.previewLink = previewLink;
            return this;
        }

        public Builder addThumbnail(Bitmap image) {
            this.thumbnail = image;
            return this;
        }

        public Book build() {
            //Here we create the actual bank account object, which is always in a fully initialised state when it's returned.
            Book book = new Book();
            book.title = this.title;
            book.authors = this.authors;
            book.publisher = this.publisher;
            book.publishedDate = this.publishedDate;
            book.ratings = this.ratings;
            book.description = this.description;
            book.identifiers = this.identifiers;
            book.previewLink = this.previewLink;
            book.pageCount = this.pageCount;
            book.thumbnail = this.thumbnail;
            book.imageLink = this.imageLink;
            return book;
        }

    }
}
