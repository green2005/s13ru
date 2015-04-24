package com.parser;

public enum FragmentMenuItem {

    NEWS_ITEM(R.string.news, R.drawable.ic_drawer),
    POSTERS_ITEM(R.string.posters, R.drawable.ic_launcher),
    VK_ITEM(R.string.vk, R.drawable.ic_drawer),
    ADVERTS_ITEM(R.string.adverts, R.drawable.ic_drawer);

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
