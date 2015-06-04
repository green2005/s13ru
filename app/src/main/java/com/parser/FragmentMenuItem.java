package com.parser;

public enum FragmentMenuItem {

    NEWS_ITEM(R.string.news, R.drawable.ic_news),
    POSTER_ITEM(R.string.posters, R.drawable.ic_posters),
    VK_ITEM(R.string.vk, R.drawable.ic_vk),
    SETTINGS_ITEM(R.string.settings, R.drawable.ic_settings);

    private int mImageResId;
    private int mNameResId;

    FragmentMenuItem(int nameResId, int imageId) {
        mNameResId = nameResId;
        mImageResId = imageId;
    }

    public int getImageResourceId() {
        return mImageResId;
    }

    public int getNameResourceId() {
        return mNameResId;
    }

}
